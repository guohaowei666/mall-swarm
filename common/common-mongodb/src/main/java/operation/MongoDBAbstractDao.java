package operation;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.data.support.PageableExecutionUtils;
import util.GenericsUtils;

import java.util.List;

public abstract class MongoDBAbstractDao<T> {
    @Autowired
    private MongoTemplate mongoTemplate;
    /**
     * DAO所管理的Entity类型.
     */
    protected Class<T> entityClass;

    protected Class<T> getEntityClass() {
        return entityClass;
    }

    public MongoDBAbstractDao() {
        entityClass = GenericsUtils.getGenericClass(getClass());
    }

    /**
     * 添加信息
     *
     * @param entityClass
     */
    public void insert(T entityClass) {
        this.mongoTemplate.insert(entityClass);
    }

    /**
     * 根据主键删除信息
     *
     * @param id
     */
    public void deleteByPrimaryKey(Long id) {
        Query query = new Query(Criteria.where("id").is(id));
        this.mongoTemplate.remove(query, getEntityClass());
    }

    /**
     * 根据条件删除记录
     *
     * @param criteria
     */
    public void deleteByCriteria(Criteria criteria) {
        Query query = new Query(criteria);
        this.mongoTemplate.remove(query, getEntityClass());
    }

    /**
     * 根据条件查询当前数据总量
     *
     * @param criteria
     * @return
     */
    public Long findCountByCriteria(Criteria criteria) {
        Query query = new Query(criteria);
        return this.mongoTemplate.count(query, getEntityClass());
    }

    /**
     * 根据条件查询当前数据列表
     *
     * @param criteria
     * @return
     */
    public List<T> findListByCriteria(Criteria criteria) {
        Query query = new Query(criteria);
        return this.mongoTemplate.find(query, entityClass);
    }

    /**
     * 分页查询
     *
     * @param criteria
     * @param pageNo
     * @param pageSize
     * @return
     */
    public Page<T> pageSearch(Criteria criteria, Integer pageNo, Integer pageSize) {
        Query query = new Query();
        query.addCriteria(criteria);
        Pageable pageable = PageRequest.of(pageNo - 1, pageSize);
        List<T> userList = mongoTemplate.find(query.with(pageable), entityClass);
        //
        return PageableExecutionUtils.getPage(userList, pageable,
                () -> mongoTemplate.count(query, entityClass));
    }

    /**
     * 根据条件修改mongodb信息
     *
     * @param criteria
     * @param update
     */
    public void updateByCriteria(Criteria criteria, Update update) {
        Query query = new Query();
        query.addCriteria(criteria);
        mongoTemplate.updateMulti(query, update, entityClass);
    }

}
