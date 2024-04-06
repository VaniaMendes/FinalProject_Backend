package aor.paj.bean;

import aor.paj.dao.CategoryDao;
import aor.paj.dao.TaskDao;
import aor.paj.dao.UserDao;
import aor.paj.dto.Category;
import aor.paj.entity.CategoryEntity;
import aor.paj.entity.TaskEntity;
import aor.paj.entity.UserEntity;
import aor.paj.service.DashboardService;
import jakarta.ejb.EJB;
import jakarta.ejb.Singleton;

import java.time.Duration;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Singleton
public class DashboardBean {

    @EJB
    UserDao userDao;
    @EJB
    TaskDao taskDao;
    @EJB
    CategoryBean categoryBean;

    @EJB
    CategoryDao categoryDao;


    //Devolve o total de tarefas por estado
    public Map<String, Long> countTasksByState(String username) {
        UserEntity userEntity = userDao.findUserByUsername(username);
        Map<String, Long> tasksByState = new HashMap<>();
        String[] states = {"todo", "doing", "done"}; // Estados possíveis das tarefas

        for (String state : states) {
            long count = taskDao.countTasksByStateForUser(userEntity, state);
            tasksByState.put(state, count);
        }

        return tasksByState;
    }

    //Devolve o total de tarefas por estado para todos os utilizadores
    public Map<String, Long> countTasksByStateForAllUsers() {
        Map<String, Long> tasksByState = new HashMap<>();
        String[] states = {"todo", "doing", "done"};

        // Iterar sobre todos os usuários do sistema
        List<UserEntity> allUsers = userDao.findAllUsers();
        for (UserEntity user : allUsers) {
            for (String state : states) {
                // Obter o número de tarefas no estado atual para o usuário atual
                long count = taskDao.countTasksByStateForUser(user, state);
                // Adicionar ao total acumulado para esse estado
                tasksByState.put(state, tasksByState.getOrDefault(state, 0L) + count);
            }
        }

        return tasksByState;
    }

    // Método para obter o número total de usuários
    public int getTotalUsersCount() {
        return userDao.getTotalUsersCount();
    }

    // Método para obter o número de utilizadores confirmados
    public int getConfirmedUsersCount() {
        return userDao.getConfirmedUsersCount();
    }

    //Método para obter o números de utilizadores não confirmados
    public int getUnconfirmedUsersCount() {
        return userDao.getUnconfirmedUsersCount();
    }

    // Método para obter a contagem média do número de tarefas por usuário
    public double getAverageTasksPerUser() {

        //Numero total de utilizadores
        int totalUsers = userDao.getTotalUsersCount();
        //Numero total de tarefas ativas
        int totalTasks = taskDao.findActiveTasks().size();
        double calculateAverageTasksPerUser = (double) totalTasks / totalUsers;

        return calculateAverageTasksPerUser;
    }


    // Método para listar as categorias ordenadas por número de tarefas
    public Map<String, Long> getCategoriesOrderedByTaskCount() {

        List<CategoryEntity> categoryEntities = taskDao.getCategoriesOrderedByTaskCount();
      //Crio um hasmap para guardar o titulo e o numero de tasks
        Map<String, Long> categories = new HashMap<>();

        for (CategoryEntity categoryEntity : categoryEntities) {
            String categoryName = categoryEntity.getTitle();
            long taskCount = taskDao.countTasksByCategory(categoryEntity.getTitle());
            categories.put(categoryName, taskCount);

        }

        // Ordenar o mapa por valor em ordem decrescente
        Map<String, Long> sortedCategories = categories.entrySet().stream()
                .sorted(Map.Entry.<String, Long>comparingByValue().reversed())
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e1, LinkedHashMap::new));

        return sortedCategories;
    }

    public double getAverageTaskCompletionTime() {
        List<TaskEntity> tasks = taskDao.findActiveTasks();
        long totalCompletionTime = 0;
        int count = 0;

        for (TaskEntity task : tasks) {
            if ("done".equals(task.getState()) && task.getConslusionDate() != null) {
                LocalDate startDate = task.getInitialDate();
                LocalDate endDate = task.getConslusionDate();
                long daysBetween = Duration.between(startDate.atStartOfDay(), endDate.atStartOfDay()).toDays();
                totalCompletionTime += daysBetween;
                count++;

            }
        }
        return totalCompletionTime/count;
    }



    // Método para obter o número de utilizadores registados ao longo do tempo
    public Map<LocalDate, Integer> getUsersRegisteredOverTime() {
        List<UserEntity> users = userDao.findAllUsers();
        Map<LocalDate, Integer> usersRegisteredOverTime = new TreeMap<>();
        Map<LocalDate, Integer> cumulativeUsersRegisteredOverTime = new LinkedHashMap<>();
        int cumulativeCount = 0;

        for (UserEntity user : users) {
            LocalDate registrationDate = user.getRegisterDate();
            if (registrationDate != null) {
                usersRegisteredOverTime.put(registrationDate, usersRegisteredOverTime.getOrDefault(registrationDate, 0) + 1);
            }
        }

        // Cálculo da contagem cumulativa ao longo do tempo
        for (Map.Entry<LocalDate, Integer> entry : usersRegisteredOverTime.entrySet()) {
            cumulativeCount += entry.getValue();
            cumulativeUsersRegisteredOverTime.put(entry.getKey(), cumulativeCount);
        }

        return cumulativeUsersRegisteredOverTime;
    }


    //Método para obter o numero total de tarefas ao longo do tempo
    public Map<LocalDate, Integer> getTasksConcludedOverTime() {
        List<TaskEntity> tasks = taskDao.findCompletedTasks();
        Map<LocalDate, Integer> tasksConclusionOverTime = new TreeMap<>();

        for (TaskEntity task : tasks) {
            LocalDate conclusionDate = task.getConslusionDate();
            tasksConclusionOverTime.put(conclusionDate, tasksConclusionOverTime.getOrDefault(conclusionDate, 0) + 1);
        }

        int cumulativeCount = 0;
        Map<LocalDate, Integer> cumulativeTasksConclusionOverTime = new TreeMap<>();
        for (Map.Entry<LocalDate, Integer> entry : tasksConclusionOverTime.entrySet()) {
            cumulativeCount += entry.getValue();
            cumulativeTasksConclusionOverTime.put(entry.getKey(), cumulativeCount);
        }

        return cumulativeTasksConclusionOverTime;
    }
}
