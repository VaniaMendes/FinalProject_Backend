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

	public UserEntity findUserByTokenConfirmation(String tokenConfirmation){
		try{
			return (UserEntity) em.createNamedQuery("User.findUserByTokenConfirmation").setParameter("tokenConfirmation", tokenConfirmation).getSingleResult();
		}catch(NoResultException e){
			return null;
		}
	}

	public List<UserEntity> findUsersByFirstNameStartingWith(String prefix) {
		try {
			return em.createNamedQuery("User.findUserByNameStartingWith", UserEntity.class)
					.setParameter("prefix", prefix + "%")
					.getResultList();
		} catch (NoResultException e) {
			return null;
		}
	}

	public List<UserEntity> findUsersByEmailStartingWith(String prefix) {
		try {
			return em.createNamedQuery("User.findUserByEmailStartingWith", UserEntity.class)
					.setParameter("prefix", prefix + "%")
					.getResultList();
		} catch (NoResultException e) {
			return null;// ou retorna null
		}
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


	public UserEntity findUserByEmail(String email){
		try{
			return (UserEntity) em.createNamedQuery("User.findUserByEmail").setParameter("email", email).getSingleResult();

		}catch(NoResultException e){
			return null;
		}
	}

	//Método para devolver uma lista com todos os utilizadores
	public List<UserEntity> findAllUsers (){
		try{
			return em.createNamedQuery("User.findAllUsers", UserEntity.class).getResultList();
		}catch(NoResultException e){
			return null;
		}
	}

	public int getConfirmedUsersCount() {
		return em.createNamedQuery("User.confirmedUsers", Long.class)
				.getSingleResult()
				.intValue();
	}

	public int getUnconfirmedUsersCount() {
		return em.createNamedQuery("User.unconfirmedUsers", Long.class)
				.getSingleResult()
				.intValue();
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


	// Método para buscar o número total de utilizadores
	public int getTotalUsersCount() {
		return em.createNamedQuery("User.totalUsers", Long.class)
				.getSingleResult()
				.intValue();
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




