@file:Suppress("NAME_SHADOWING")

package lab.mars.rl.algo.func_approx

import lab.mars.rl.algo.func_approx.FunctionApprox.Companion.log
import lab.mars.rl.algo.ntd.MAX_N
import lab.mars.rl.model.State
import lab.mars.rl.model.ValueFunction
import lab.mars.rl.util.buf.newBuf
import lab.mars.rl.util.debug
import lab.mars.rl.util.sum
import org.apache.commons.math3.util.FastMath.min
import org.apache.commons.math3.util.FastMath.pow

fun FunctionApprox.`n-step semi-gradient TD`(n: Int, v: ValueFunction) {
    val _R = newBuf<Double>(min(n, MAX_N))
    val _S = newBuf<State>(min(n, MAX_N))
    for (episode in 1..episodes) {
        log.debug { "$episode/$episodes" }
        var n = n
        var T = Int.MAX_VALUE
        var t = 0
        var s = started.rand()
        var a = s.actions.rand(policy(s))
        _R.clear();_R.append(0.0)
        _S.clear();_S.append(s)
        do {
            if (t >= n) {//最多存储n个
                _R.removeFirst()
                _S.removeFirst()
            }
            if (t < T) {
                val (s_next, reward, _) = a.sample()
                _R.append(reward)
                _S.append(s_next)
                s = s_next
                if (s.isTerminal()) {
                    T = t + 1
                    val _t = t - n + 1
                    if (_t < 0) n = T //n is too large, normalize it
                } else
                    a = s.actions.rand(policy(s))
            }
            val _t = t - n + 1
            if (_t >= 0) {
                var G = sum(1..min(n, T - _t)) { pow(gamma, it - 1) * _R[it] }
                if (_t + n < T) G += pow(gamma, n) * v[_S[n]]
                v.update(_S[0], alpha * (G - v[_S[0]]))
            }
            t++
        } while (_t < T - 1)
        log.debug { "n=$n,T=$T" }
        episodeListener(episode)
    }
}