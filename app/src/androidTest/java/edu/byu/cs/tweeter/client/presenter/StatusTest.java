package edu.byu.cs.tweeter.client.presenter;

import org.junit.Before;
import org.testng.annotations.Test;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import edu.byu.cs.tweeter.client.model.service.StatusService;
import edu.byu.cs.tweeter.client.observer.PostStatusServiceObserver;
import edu.byu.cs.tweeter.model.domain.User;

public class StatusTest {

    private MainPresenter.View mockMainView;
    private StatusService mockStatusService;
    private MainPresenter mainActivityPresenterSpy;

    @Before
    public void setup() {

        mockMainView = Mockito.mock(MainPresenter.View.class);
        mockStatusService = Mockito.mock(StatusService.class);

        User user = new User();                                                                         // made public

        mainActivityPresenterSpy = Mockito.spy(new MainPresenter(mockMainView));
        Mockito.doReturn(mockStatusService).when(mainActivityPresenterSpy).getStatusService();
    }

    @org.junit.Test
    @Test
    public void testPostStatus_postSucceeds() {
        Answer<Void> postSucceedAnswer = new Answer<Void>() {
            @Override
            public Void answer(InvocationOnMock invocation) throws Throwable {
                PostStatusServiceObserver observer = invocation.getArgument(2);
                observer.handlePostStatusSuccess();
                return null;
            }
        };

        Mockito.doAnswer(postSucceedAnswer).when(mockStatusService).PostStatus(Mockito.any(), Mockito.any(), Mockito.any());

        mainActivityPresenterSpy.handlePostStatusSuccess();
        //Mockito.verify(mockMainView).cancelPostToast();
        Mockito.verify(mockMainView).displayInfoMessage("Successfully Posted!");

    }



}
