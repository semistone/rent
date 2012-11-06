package org.siraya.rent.user.dao;

import org.apache.ibatis.annotations.Insert;
import org.siraya.rent.pojo.HttpRetryQueue;
import org.springframework.stereotype.Repository;

@Repository("httpRetryQueueDao")
public interface IHttpRetryQueueDao {
    @Insert("insert into HTTP_RETRY_QUEUE values(#{id},#{url},#{maxRetry},#{status},#{created},#{modified})")
    public void newEntity(HttpRetryQueue queue);
}
