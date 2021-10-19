package com.evanisnor.freshwaves.features.updater

import java.time.Instant
import java.time.ZoneId

fun Instant.startOfDayUTC(): Instant = atZone(ZoneId.of("UTC"))
    .withHour(0)
    .withMinute(0)
    .withSecond(0)
    .toInstant()