package aor.paj.dao;

import aor.paj.dto.LoginDto;
import aor.paj.entity.UserEntity;
import jakarta.ejb.Stateless;
import jakarta.persistence.NoResultException;

import java.util.List;

@Stateless
public class UserDao extends AbstractDao<UserEntity> {

	private static final long serialVersionUID = 1L;

	public UserDao() {
		super(UserEntity.class);
	}



	public UserEntity findUserByUsername(String username) {
		try {
			return (UserEntity) em.createNamedQuery("User.findUserByUsername").setParameter("username", username)
					.getSingleResult();
		} catch (NoResultException e) {
			return null;
		}
	}

	public UserEntity findUserByToken(String token){
		try{
			return (UserEntity) em.createNamedQuery("User.findUserByToken").setParameter("token", token).getSingleResult();

		}catch (NoResultException e){
			return null;
		}
	}
	public UserEntity findUserByName(String name){
		try{
			return (UserEntity) em.createNamedQuery("User.findUserByName").setParameter("name", name).getResultList();
		}catch(NoResultException e){
			return null;
		}
	}

	public UserEntity findUserByEmail(String email){
		try{
			return (UserEntity) em.createNamedQuery("User.findUserByEmail").setParameter("email", email).getSingleResult();

		}catch(NoResultException e){
			return null;
		}
	}

	public List<UserEntity> findAllUsers (){
		try{
			return em.createNamedQuery("User.findAllUsers", UserEntity.class).getResultList();
		}catch(NoResultException e){
			return null;
		}
	}

	public boolean update(UserEntity userEntity) {
		try {
			em.merge(userEntity);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	public boolean removed(UserEntity userEntity){
		try{
			em.remove(userEntity);
			return true;
		}catch(Exception e){
			e.printStackTrace();
			return false;
		}
	}

	public boolean removedToken(UserEntity userEntity){
		try{
			userEntity.setToken(null);
			em.merge(userEntity);
			return true;
		}catch(Exception e){
			e.printStackTrace();
			return false;
		}
	}
}




