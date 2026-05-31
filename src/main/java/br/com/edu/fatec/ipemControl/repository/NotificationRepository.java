package br.com.edu.fatec.ipemControl.repository;

import br.com.edu.fatec.ipemControl.entity.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Integer> {

    // Notificações do usuário ordenadas por data
    List<Notification> findByUserRegistrationOrderByCreatedAtDesc(Integer registration);

    // Notificações não lidas do usuário
    List<Notification> findByUserRegistrationAndReadFalseOrderByCreatedAtDesc(Integer registration);

    // Conta não lidas do usuário (badge do sino)
    long countByUserRegistrationAndReadFalse(Integer registration);

    // Marca todas como lidas (#U03)
    @Modifying
    @Transactional
    @Query("UPDATE Notification n SET n.read = true WHERE n.user.registration = :registration")
    void markAllAsRead(@Param("registration") Integer registration);
}