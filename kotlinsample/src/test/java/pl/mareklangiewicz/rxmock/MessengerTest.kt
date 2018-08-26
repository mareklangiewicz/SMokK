package pl.mareklangiewicz.rxmock

import com.jakewharton.rxrelay2.PublishRelay
import io.reactivex.schedulers.TestScheduler
import org.junit.Test
import org.junit.runner.RunWith
import pl.mareklangiewicz.uspek.USpek.o
import pl.mareklangiewicz.uspek.USpek.uspek
import pl.mareklangiewicz.uspek.USpekJUnitRunner
import pl.mareklangiewicz.uspek.eq
import java.io.IOException
import java.util.concurrent.TimeUnit.SECONDS

@RunWith(USpekJUnitRunner::class)
class MessengerTest {

    @Test
    fun messengerTest() {

        setupRxJavaErrorHandler()

        uspek("messenger test") {

            val sendMessageEventS = PublishRelay.create<Message>()
            val apiSendMessage = RxMockCompletable1<Message>()
            val apiReceiveNewMessages = RxMockMaybe0<List<Message>>()
            val refreshScheduler = TestScheduler()

            val resultS = messenger(
                    sendMessageEventS,
                    apiSendMessage,
                    apiReceiveNewMessages,
                    refreshScheduler
            ).test()

            "On start" o {
                "no results yet" o { resultS.assertEmpty() }
                "no api calls yet" o {
                    apiSendMessage.invocations.size eq 0
                    apiReceiveNewMessages.invocations eq 0
                }
            }

            "On send message event" o {
                sendMessageEventS put "Hello"

                "call api to send message" o { apiSendMessage.invocations hasOne "Hello" }
                "do not display any result yet" o { resultS.assertEmpty() }

                "On sending success" o {
                    apiSendMessage.onComplete()

                    "display confirmation message" o { resultS isNow "Sent: Hello" }
                }

                "On sending error" o {
                    apiSendMessage.onError(IOException())

                    "display error message" o { resultS isNow "Error sending: Hello" }

                    "On second sending attempt" o {
                        sendMessageEventS put "Is there anybody in there?"

                        "call api to send new message" o {
                            apiSendMessage.invocations hasOne "Is there anybody in there?"
                        }

                        "On second sending success" o {
                            apiSendMessage.onComplete()

                            "display confirmation message" o { resultS isNow "Sent: Is there anybody in there?" }
                        }
                    }
                }
            }

            "On 4 seconds" o {
                refreshScheduler.advanceTimeBy(4, SECONDS)

                "do not call api to receive new messages yet" o { apiReceiveNewMessages.invocations eq 0 }

                "On 5 seconds" o {
                    refreshScheduler.advanceTimeBy(1, SECONDS)

                    "call api to receive new messages" o { apiReceiveNewMessages.invocations eq 1 }
                    "do not display any result yet" o { resultS.assertEmpty() }

                    "On no new messages received" o {
                        apiReceiveNewMessages.onComplete()

                        "do not display any result" o { resultS.assertEmpty() }
                    }

                    "On one new message received" o {
                        apiReceiveNewMessages put listOf("Hey")

                        "display received message" o { resultS isNow "Received: Hey" }
                    }

                    "On network error" o {
                        apiReceiveNewMessages.onError(IOException())

                        "display error message" o { resultS isNow "Error receiving new messages" }
                    }
                }
            }

        }
    }
}