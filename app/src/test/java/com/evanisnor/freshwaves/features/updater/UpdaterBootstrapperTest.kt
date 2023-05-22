package com.evanisnor.freshwaves.features.updater

import com.evanisnor.freshwaves.features.updater.localbroadcast.LocalBroadcastDelegate
import com.evanisnor.freshwaves.features.updater.workmanager.FakeWorkManager
import java.time.DayOfWeek
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.temporal.TemporalAdjusters
import org.junit.Test

/**
 * Tests for [UpdaterBootstrapper]
 */
class UpdaterBootstrapperTest {

  private val workManager = FakeWorkManager()
  private val localBroadcastDelegate = object : LocalBroadcastDelegate {
    override fun register(action: String, receiver: () -> Unit) {
      receiver()
    }
  }

  private val updaterBootstrapper = UpdaterBootstrapper(workManager, localBroadcastDelegate)

  @Test
  fun `updateNow - when called - worker job is queued` () {
    updaterBootstrapper.updateNow()

    assert(workManager.queue.isNotEmpty())
  }

  @Test
  fun `registerForSuccessfulAuthorization - when auth succeeds - worker job is queued` () {
    updaterBootstrapper.registerForSuccessfulAuthorization()

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
  fun `scheduleNextUpdate - when called - returns next scheduled Instant` () {
    val expectedNextRun = ZonedDateTime.now(ZoneId.systemDefault())
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
