package org.example.mediaconnect.service;

import org.example.mediaconnect.event.UserDeletedEvent;
import org.example.mediaconnect.model.Show;
import org.example.mediaconnect.model.UserShow;
import org.example.mediaconnect.repository.ShowRepository;
import org.example.mediaconnect.repository.UserShowRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@DataJpaTest
@Import(MediaDeletionService.class)
@TestPropertySource(properties = {
        "spring.datasource.url=jdbc:h2:mem:testdb",
        "spring.datasource.driverClassName=org.h2.Driver",
        "spring.datasource.username=sa",
        "spring.datasource.password=",
        "spring.jpa.database-platform=org.hibernate.dialect.H2Dialect",
        "spring.jpa.hibernate.ddl-auto=create-drop",
        "spring.jpa.show-sql=false",
        "spring.h2.console.enabled=false"
})
class MediaDeletionServiceIntegrationTest {

    @Autowired
    private UserShowRepository userShowRepository;

    @Autowired
    private ShowRepository showRepository;

    @Autowired
    private MediaDeletionService mediaDeletionService;

    @MockitoBean
    private RabbitTemplate rabbitTemplate;

    private Show testShow1;
    private Show testShow2;

    @BeforeEach
    void setUp() {
        testShow1 = new Show();
        testShow1.setTmdbId(1396);
        testShow1.setName("Breaking Bad");
        testShow1.setOverview("A chemistry teacher turns to cooking methamphetamine");
        testShow1.setPosterPath("/ggFHVNvVeFwOOSN3DXsFIAGVM1.jpg");
        testShow1.setBackdropPath("/xnopI5Cer5bBjHJsXiFQa9xAaP.jpg");
        testShow1.setVoteAverage(9.5f);
        testShow1.setFirstAirDate("2008-01-20");
        testShow1.setNumberOfSeasons(5);
        testShow1.setNumberOfEpisodes(62);
        testShow1.setStatus("Ended");
        testShow1.setType("Scripted");
        testShow1.setCreatedAt(LocalDateTime.now());
        testShow1.setUpdatedAt(LocalDateTime.now());
        testShow1 = showRepository.save(testShow1);

        testShow2 = new Show();
        testShow2.setTmdbId(1399);
        testShow2.setName("Game of Thrones");
        testShow2.setOverview("Medieval fantasy");
        testShow2.setPosterPath("/poster.jpg");
        testShow2.setBackdropPath("/backdrop.jpg");
        testShow2.setVoteAverage(9.2f);
        testShow2.setFirstAirDate("2011-04-17");
        testShow2.setNumberOfSeasons(8);
        testShow2.setNumberOfEpisodes(73);
        testShow2.setStatus("Ended");
        testShow2.setType("Scripted");
        testShow2.setCreatedAt(LocalDateTime.now());
        testShow2.setUpdatedAt(LocalDateTime.now());
        testShow2 = showRepository.save(testShow2);
    }

    private UserShow createUserShow(String userId, int tmdbId) {
        UserShow userShow = new UserShow();
        userShow.setUserId(userId);
        userShow.setTmdbId(tmdbId);
        userShow.setSavedAt(LocalDateTime.now());
        userShow.setUpdatedAt(LocalDateTime.now());
        return userShowRepository.save(userShow);
    }

    @Test
    void testHandleUserDeletion_SuccessfulDeletion() {
        String userId = "user123";

        createUserShow(userId, testShow1.getTmdbId());
        createUserShow(userId, testShow2.getTmdbId());

        List<UserShow> beforeDeletion = userShowRepository.findByUserId(userId);
        assertEquals(2, beforeDeletion.size());

        UserDeletedEvent event = new UserDeletedEvent();
        event.setUserId(userId);
        mediaDeletionService.handleUserDeletion(event);

        List<UserShow> afterDeletion = userShowRepository.findByUserId(userId);
        assertTrue(afterDeletion.isEmpty());

        verify(rabbitTemplate, times(1)).convertAndSend(
                eq("user.events"),
                eq("user.delete.response"),
                (Object) argThat(e -> e instanceof UserDeletedEvent &&
                        ((UserDeletedEvent) e).getUserId().equals(userId) &&
                        "COMPLETED".equals(((UserDeletedEvent) e).getStatus()))
        );
    }

    @Test
    void testHandleUserDeletion_MultipleUsers() {
        String userId1 = "user123";
        String userId2 = "user456";

        createUserShow(userId1, testShow1.getTmdbId());
        createUserShow(userId1, testShow2.getTmdbId());
        createUserShow(userId2, testShow1.getTmdbId());

        assertEquals(2, userShowRepository.findByUserId(userId1).size());
        assertEquals(1, userShowRepository.findByUserId(userId2).size());

        UserDeletedEvent event = new UserDeletedEvent();
        event.setUserId(userId1);
        mediaDeletionService.handleUserDeletion(event);

        assertTrue(userShowRepository.findByUserId(userId1).isEmpty());
        assertEquals(1, userShowRepository.findByUserId(userId2).size());
    }

    @Test
    void testHandleUserDeletion_UserWithNoShows() {
        String userId = "newUser";

        UserDeletedEvent event = new UserDeletedEvent();
        event.setUserId(userId);
        mediaDeletionService.handleUserDeletion(event);

        verify(rabbitTemplate, times(1)).convertAndSend(
                eq("user.events"),
                eq("user.delete.response"),
                (Object) argThat(e -> e instanceof UserDeletedEvent &&
                        "COMPLETED".equals(((UserDeletedEvent) e).getStatus()))
        );

        assertTrue(userShowRepository.findByUserId(userId).isEmpty());
    }

    @Test
    void testHandleUserDeletion_LargeNumberOfShows() {
        String userId = "user123";

        // Create multiple shows for the large test
        int[] tmdbIds = new int[100];
        for (int i = 0; i < 100; i++) {
            Show show = new Show();
            show.setTmdbId(2000 + i);
            show.setName("Show " + i);
            show.setOverview("Test show " + i);
            show.setPosterPath("/poster" + i + ".jpg");
            show.setBackdropPath("/backdrop" + i + ".jpg");
            show.setVoteAverage(8.0f);
            show.setFirstAirDate("2020-01-01");
            show.setNumberOfSeasons(1);
            show.setNumberOfEpisodes(10);
            show.setStatus("Ended");
            show.setType("Scripted");
            show.setCreatedAt(LocalDateTime.now());
            show.setUpdatedAt(LocalDateTime.now());
            Show savedShow = showRepository.save(show);
            tmdbIds[i] = savedShow.getTmdbId();
        }

        // Create UserShow records for all the shows
        for (int tmdbId : tmdbIds) {
            createUserShow(userId, tmdbId);
        }

        assertEquals(100, userShowRepository.findByUserId(userId).size());

        UserDeletedEvent event = new UserDeletedEvent();
        event.setUserId(userId);
        mediaDeletionService.handleUserDeletion(event);

        assertTrue(userShowRepository.findByUserId(userId).isEmpty());

        verify(rabbitTemplate, times(1)).convertAndSend(
                eq("user.events"),
                eq("user.delete.response"),
                (Object) argThat(e -> "COMPLETED".equals(((UserDeletedEvent) e).getStatus()))
        );
    }

    @Test
    void testHandleUserDeletion_SuccessResponseFormat() {
        String userId = "user123";

        createUserShow(userId, testShow1.getTmdbId());

        UserDeletedEvent event = new UserDeletedEvent();
        event.setUserId(userId);
        mediaDeletionService.handleUserDeletion(event);

        verify(rabbitTemplate, times(1)).convertAndSend(
                eq("user.events"),
                eq("user.delete.response"),
                (Object) argThat(e -> {
                    UserDeletedEvent response = (UserDeletedEvent) e;
                    return response.getUserId().equals(userId) &&
                            "COMPLETED".equals(response.getStatus()) &&
                            response.getErrorMessage() == null;
                })
        );
    }

    @Test
    void testHandleUserDeletion_ErrorHandling() {
        String userId = "user123";

        createUserShow(userId, testShow1.getTmdbId());

        UserDeletedEvent event = new UserDeletedEvent();
        event.setUserId(userId);
        mediaDeletionService.handleUserDeletion(event);

        verify(rabbitTemplate, atLeastOnce()).convertAndSend(
                eq("user.events"),
                eq("user.delete.response"),
                any(UserDeletedEvent.class)
        );
    }

    @Test
    void testHandleUserDeletion_PreservesOtherUsersShowsIntegrity() {
        String userToDelete = "userDelete";
        String userToKeep1 = "userKeep1";
        String userToKeep2 = "userKeep2";

        for (String userId : new String[]{userToDelete, userToKeep1, userToKeep2}) {
            createUserShow(userId, testShow1.getTmdbId());
            createUserShow(userId, testShow2.getTmdbId());
        }

        assertEquals(2, userShowRepository.findByUserId(userToDelete).size());
        assertEquals(2, userShowRepository.findByUserId(userToKeep1).size());
        assertEquals(2, userShowRepository.findByUserId(userToKeep2).size());

        UserDeletedEvent event = new UserDeletedEvent();
        event.setUserId(userToDelete);
        mediaDeletionService.handleUserDeletion(event);

        assertTrue(userShowRepository.findByUserId(userToDelete).isEmpty());
        assertEquals(2, userShowRepository.findByUserId(userToKeep1).size());
        assertEquals(2, userShowRepository.findByUserId(userToKeep2).size());
    }
}