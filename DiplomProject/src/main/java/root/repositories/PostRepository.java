package root.repositories;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import root.enums.ModerationStatus;
import root.model.Post;
import java.util.Date;

public interface PostRepository extends PagingAndSortingRepository<Post, Long> {

    /********************************************************************** ПОИСК ПОСТОВ *************************/
    /********* ДЕФОЛТНЫЙ ПОИСК ПОСТОВ *********/
    Page<Post> findAllByIsActiveAndModerationStatusAndTimeBeforeOrderById(
            byte isActive, ModerationStatus moderationStatus, Date time, Pageable p);

    /**
     * Метод находит пост активный, допущенный, неотложенный по id.
     * @return Post
     */
    @Query(value = "SELECT p FROM Post p " +
            "WHERE p.isActive = 1 " +
            "AND p.moderationStatus = 'ACCEPTED' " +
            "AND p.time < CURRENT_TIME " +
            "AND p.id = :id")
    Post findById(@Param("id")int id);

    /********* ПОИСК САМЫХ НОВЫХ ПОСТОВ *********/
    @Query("SELECT p FROM Post p WHERE p.isActive = 1 AND p.moderationStatus = 'ACCEPTED' AND p.time < CURRENT_TIME " +
            "ORDER BY p.time DESC")
    Page<Post> findRecent(Pageable p);

    /********* ПОИСК САМЫХ СТАРЫХ ПОСТОВ *********/
    @Query("SELECT p FROM Post p WHERE p.isActive = 1 AND p.moderationStatus = 'ACCEPTED' AND p.time < CURRENT_TIME " +
            "ORDER BY p.time ASC")
    Page<Post> findEarly(Pageable p);

    /********* ПОИСК ПОСТОВ С НАИБОЛЬШИМ КОЛИЧЕСТВОМ КОММЕНТАРИЕВ *********/
    @Query(value = "SELECT * FROM posts p " +
            "JOIN (SELECT post_id, count(*) comments_amount FROM post_comments GROUP BY post_id) nt " +
            "ON p.id = nt.post_id " +
            "WHERE p.is_active = 1 AND p.moderation_status = 'ACCEPTED' AND p.time < CURRENT_TIME " +
            "ORDER BY nt.comments_amount DESC",
            nativeQuery = true)
    Page<Post> findPopular(Pageable p);

    /********* ПОИСК ПОСТОВ С НАИБОЛЬШИМ КОЛИЧЕСТВОМ ЛАЙКОВ *********/
    @Query(value = "SELECT * FROM posts p " +
            "JOIN (SELECT post_id, count(*) likes_amount FROM post_votes  WHERE value > 0 GROUP BY post_id) nt " +
            "ON p.id = nt.post_id " +
            "WHERE p.is_active = 1 AND p.moderation_status = 'ACCEPTED' AND p.time < CURRENT_TIME " +
            "ORDER BY nt.likes_amount DESC",
            nativeQuery = true)
    Page<Post> findBest(Pageable p);

    /********* ПОИСК ПОСТОВ ЗА ОПРЕДЕЛЕННЫЙ ДЕНЬ *********/
    Page<Post> findAllByIsActiveAndModerationStatusAndTimeBeforeAndTimeBetweenOrderByTimeDesc(
            byte isActive, ModerationStatus ms, Date time, Date d1, Date d2, Pageable p);

    /********* ПОИСК ПОСТОВ ПО ТЕГУ *********/
    @Query(value = "SELECT * FROM posts " +
            "JOIN tag2post ON posts.id = tag2post.post_id " +
            "JOIN tags ON tag2post.tag_id = tags.id " +
            "WHERE posts.is_active = 1 " +
            "AND posts.moderation_status = 'ACCEPTED' " +
            "AND posts.time < CURRENT_TIME " +
            "AND tags.title = :tag",
            nativeQuery = true)
    Page<Post> findbyTag(@Param("tag")String tag, Pageable p);

    /********* ПОИСК ПОСТОВ ПО СТАТУСУ (NEW) *********/
    @Query(value = "SELECT * FROM posts " +
            "WHERE posts.is_active = 1 " +
            "AND posts.moderation_status = :status ",
            nativeQuery = true)
    Page<Post> findByStatusAndModeratorId(@Param("status") String status, Pageable p);

    /**
     * Поиск постов, проверенных определённым модератором и статусу поста.
     * @param status - отклонённые (DECLINED) или принятые (ACCEPTED) посты.
     * @param moderId - id модератора.
     * @param p - Параметры пагинации.
     * @return Page.
     */
    @Query(value = "SELECT * FROM posts " +
            "WHERE posts.is_active = 1 " +
            "AND posts.moderation_status = :status " +
            "AND posts.moderator_id = :moderId",
            nativeQuery = true)
    Page<Post> findByStatusAndModeratorId(@Param("status") String status, @Param("moderId") int moderId, Pageable p);

    /**
     * Поиск неопубликованных постов текущего юзера.
     * @param currentUserId - ID текущего авторизованного юзера.
     * @param p - Параметры пагинации.
     * @return Page.
     */
    @Query(value = "SELECT * FROM posts WHERE user_id = :currentUserId AND is_active = 0",
            nativeQuery = true)
    Page<Post> findInactive(@Param("currentUserId")int currentUserId, Pageable p);

    /**
     * Поиск постов текущего юзера, ожидающих модерации (NEW), отклонённых модератором (DECLINED), опубликованных (ACCEPTED).
     * @param currentUserId - ID текущего авторизованного юзера.
     * @param p - Параметры пагинации.
     * @return Page.
     */
    @Query(value = "SELECT * FROM posts WHERE user_id = :currentUserId AND moderation_status = :status AND is_active = 1",
            nativeQuery = true)
    Page<Post> findByStatusAndUserId(@Param("currentUserId")int currentUserId, @Param("status")String status, Pageable p);

    /********************************************************************** ПОДСЧЁТ ПОСТОВ *************************/
    /********* ПОИСК КОЛИЧЕСТВА ПОСТОВ ПО ТЕГУ *********/
    @Query(value = "SELECT COUNT(*) FROM posts " +
            "JOIN tag2post ON posts.id = tag2post.post_id " +
            "JOIN tags ON tag2post.tag_id = tags.id " +
            "WHERE posts.is_active = 1 " +
            "AND posts.moderation_status = 'ACCEPTED' " +
            "AND posts.time < CURRENT_TIME " +
            "AND tags.title = :tag",
            nativeQuery = true)
    int amountWithTag(@Param("tag")String tag);

    /********* ПОИСК КОЛИЧЕСТВА ПОСТОВ *********/
    @Query(value = "SELECT COUNT(*) FROM posts " +
            "WHERE posts.is_active = 1 " +
            "AND posts.moderation_status = 'ACCEPTED' " +
            "AND posts.time < CURRENT_TIME ",
            nativeQuery = true)
    int amountTotal();

    /********* ПОИСК КОЛИЧЕСТВА ПОСТОВ ПО СТАТУСУ МОДЕРАЦИИ *********/
    @Query(value = "SELECT COUNT(*) FROM posts " +
            "WHERE posts.moderation_status = :status " +
            "AND posts.time < CURRENT_TIME ",
            nativeQuery = true)
    int countByModerationStatus(@Param("status")String status);



}
