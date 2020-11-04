package org.folio.modusers.service;

import static java.util.Collections.singletonList;
import static java.util.stream.Collectors.toList;

import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.UUID;
import javax.persistence.EntityGraph;
import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.folio.modusers.domain.OffsetRequest;
import org.folio.modusers.domain.entity.User;
import org.folio.modusers.dto.UserDto;
import org.folio.modusers.dto.UserdataCollectionDto;
import org.folio.modusers.mapper.UserMapper;
import org.folio.modusers.repository.UserRepository;
import org.folio.spring.service.CQLService;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.util.StringUtils;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

  private final UserRepository userRepository;

  private final EntityManager entityManager;

  private final UserMapper userMapper;

  private final CQLService cqlService;

  private static final String FETCH_PROPERTIES_NAME = "javax.persistence.loadgraph";

  public UserDto getUserById(String id) {
    return userRepository.findById(UUID.fromString(id))
        .map(userMapper::mapEntityToDto)
        .orElseThrow(() -> new IllegalArgumentException("User not found"));
  }

  private static MultiValueMap<String, String> filter(String id, String userId) {
    LinkedMultiValueMap<String, String> map = new LinkedMultiValueMap<>();
    map.put(id, singletonList(userId));
    return map;
  }

  public List<UserDto> getUsersByCriteria(int offset, int limit,
      MultiValueMap<String, String> filters, List<String> include) {
    CriteriaBuilder builder = entityManager.getCriteriaBuilder();

    CriteriaQuery<User> criteria = builder.createQuery(User.class);
    Root<User> root = criteria.from(User.class);

    List<Predicate> predicates = new ArrayList<>();
    if (filters != null) {
      for (Entry<String, List<String>> filter : filters.entrySet()) {
        if (filter.getValue().size() == 1) {
          predicates.add(
              builder.equal(root.get(filter.getKey()), filter.getValue().get(0)));
        } else {
          predicates.add(builder.or((Predicate[]) filter.getValue().stream()
              .map(e -> root.get(filter.getKey())).toArray()));
        }
      }
    }

    criteria.where(builder.and(predicates.toArray(new Predicate[0])));

    EntityGraph graph = entityManager.createEntityGraph(User.class);
    include.forEach(graph::addSubgraph);

    List<User> users = entityManager
        .createQuery(criteria)
        .setHint(FETCH_PROPERTIES_NAME, graph)
        .setFirstResult(offset)
        .setMaxResults(limit)
        .getResultList();
    return users.stream().map(userMapper::mapEntityToDto).collect(toList());
  }

  public void removeById(final String id) {
    userRepository.deleteById(UUID.fromString(id));
  }

  public UserDto saveUser(final UserDto userDto) {
    User user = userMapper.mapDtoToEntity(userDto);
    return userMapper.mapEntityToDto(userRepository.save(user));
  }

  public UserdataCollectionDto getUsers(String query, int offset, int limit, String order, String lang) {
    //TODO: add order support to the cqlService
    return userMapper.mapToUserDataCollectionDto(cqlService.getByCQL(User.class, prepareCQLQueryForUsers(query), offset, limit > 0 ? limit : 100));
  }

  public void updateUser(final UserDto userDto, final String userId) {
    User user = userRepository.getOne(UUID.fromString(userId));
    userMapper.mapEntityToDto(userDto, user);
    userRepository.save(user);
  }

  public Optional<UserDto> getUserByUsername(String username,
      List<String> includes) {
    return getUsersByCriteria(0, 1, filter("username", username), includes).stream()
        .findFirst();
  }

  public Optional<UserDto> getUserById(String userId, List<String> includes) {
    return getUsersByCriteria(0, 1, filter("id", userId), includes).stream().findFirst();
  }

  public UserDto getUserByAlias(String id) {
    return userMapper
        .mapEntityToDto(userRepository.findByEmailOrPhoneOrMobilePhoneOrUsername(id, id, id, id));
  }

  //some inline fixes that must be done in a more efficient way
  private String prepareCQLQueryForUsers(String query) {
    try {
      if (StringUtils.isEmpty(query)) {
        return query;
      }

      // We do not need "sort by" in the criteria
      // TODO: Must be moved into the cqlService
      int sortby = query.indexOf("sortby");
      if (sortby > 0) {
        query = query.substring(0, sortby);
      }

      //Unfortunately we have have different names in view nad model so lets add some replacements
      // Have to be more careful with naming beforehand
      query = query
              .replaceAll("personal.", "")
              .replaceAll("preferredFirstName", "firstName")
              .replaceAll("or id=\"[\\w*]+\"", "")
              .replaceAll("or externalSystemId=\"[\\w*]+\"", "");


      return query;
    } finally {
      log.info("prepareCQLQueryForUsers: " + query);
    }
  }
}
