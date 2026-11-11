package nl.hexmaster.finalapproach.engine

import nl.hexmaster.finalapproach.domain.Aircraft

/** Finds aircraft that came too close. Every aircraft still in play counts, rolling ones included. */
object Collisions {

    /** Pairs of aircraft ids, lower id first, whose centres are closer than [separation]. */
    fun find(aircraft: List<Aircraft>, separation: Double): List<Pair<Int, Int>> = buildList {
        for (i in aircraft.indices) for (j in i + 1 until aircraft.size) {
            val a = aircraft[i]
            val b = aircraft[j]
            if (a.position.distanceTo(b.position) < separation) add(minOf(a.id, b.id) to maxOf(a.id, b.id))
        }
    }
}
