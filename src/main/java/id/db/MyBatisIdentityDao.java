package id.db;

import org.mybatis.spring.support.SqlSessionDaoSupport;

public class MyBatisIdentityDao extends SqlSessionDaoSupport implements IdentityDao {
	
	public void createIdentityRecord(RegistrationNesiUser rnu) throws Exception {
		getSqlSession().insert("create_registration_nesiuser", rnu);		
	}

}
