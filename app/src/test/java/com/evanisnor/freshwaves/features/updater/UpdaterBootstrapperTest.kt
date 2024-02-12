package com.evanisnor.freshwaves.features.updater

import com.evanisnor.freshwaves.deps.handyauth.FakeHandyAuth
import com.evanisnor.freshwaves.features.updater.workmanager.FakeWorkManager
import com.evanisnor.freshwaves.integration.crashlytics.FakeCrashlytics
import com.evanisnor.freshwaves.spotify.auth.SpotifyAuthorizationImpl
import com.evanisnor.freshwaves.user.UserStateRepository
import org.junit.Test
import java.time.DayOfWeek
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.temporal.TemporalAdjusters

/**
 * Tests for [UpdaterBootstrapper]
 */
class UpdaterBootstrapperTest {
  private val workManager = FakeWorkManager()
  private val fakeHandyAuth = FakeHandyAuth()
  private val userStateRepository = UserStateRepository(SpotifyAuthorizationImpl(fakeHandyAuth))

  private val updaterBootstrapper =
    UpdaterBootstrapper(
      workManager,
      userStateRepository,
      FakeCrashlytics(),
    )

  @Test
  fun `updateNow - when called - worker job is queued`() {
    updaterBootstrapper.updateNow()

    assert(workManager.queue.isNotEmpty())
  }

  @Test
  fun `scheduleNextUpdate - when called - worker job is queued`() {
    updaterBootstrapper.scheduleNextUpdate()

    assert(workManager.queue.isNotEmpty())
  }

  @Test
  fun `scheduleNextUpdate - when called - worker job is delayed`() {
    updaterBootstrapper.scheduleNextUpdate()

    assert(workManager.queue.first().workSpec.initialDelay > 0)
  }

  @Test
  fun `scheduleNextUpdate - when called - returns next scheduled Instant`() {
    val expectedNextRun =
      ZonedDateTime.now(ZoneId.systemDefault())
        .with(TemporalAdjusters.next(DayOfWeek.FRIDAY))
        .withHour(5)
        .withMinute(0)
        .withSecond(0)
        .withNano(0)
        .toInstant()

    val nextRun = updaterBootstrapper.scheduleNextUpdate()

    assert(nextRun == expectedNextRun)
  }
}
