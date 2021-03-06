package lab.mars.rl.algo.func_approx.on_policy

import lab.mars.rl.model.*
import lab.mars.rl.util.log.debug
import lab.mars.rl.util.matrix.times

fun <E> MDP.`Episodic semi-gradient Sarsa control`(
    q: ApproximateFunction<E>, π: Policy,
    α: Double,
    episodes: Int,
    episodeListener: (Int, Int) -> Unit = { _, _ -> },
    stepListener: (Int, Int, State, Action<State>) -> Unit = { _, _, _, _ -> }) {
  for (episode in 1..episodes) {
    log.debug { "$episode/$episodes" }
    var step = 0
    var s = started()
    var a = π(s)
    while (true) {
      step++
      stepListener(episode, step, s, a)
      val (s_next, reward) = a.sample()
      if (s_next.isNotTerminal) {
        val a_next = π(s_next)
        q.w += α * (reward + γ * q(s_next, a_next) - q(s, a)) * q.`∇`(s, a)
        s = s_next
        a = a_next
      } else {
        q.w += α * (reward - q(s, a)) * q.`∇`(s, a)
        break
      }
    }
    episodeListener(episode, step)
  }
}