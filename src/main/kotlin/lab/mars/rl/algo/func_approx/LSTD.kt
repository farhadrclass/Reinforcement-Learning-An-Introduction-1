package lab.mars.rl.algo.func_approx

import lab.mars.rl.algo.func_approx.FunctionApprox.Companion.log
import lab.mars.rl.model.impl.LinearFunc
import lab.mars.rl.util.debug
import lab.mars.rl.util.matrix.Matrix
import lab.mars.rl.util.matrix.plus
import lab.mars.rl.util.matrix.times

fun FunctionApprox.LSTD(vFunc: LinearFunc, `ε`: Double) {
    val xFeature = vFunc.x
    val d = xFeature.featureNum
    val A_ = 1 / `ε` * Matrix.identity(d)
    val b = Matrix.column(d)
    for (episode in 1..episodes) {
        log.debug { "$episode/$episodes" }
        var s = started.rand()
        var x = xFeature(s)
        while (s.isNotTerminal()) {
            val a = s.actions.rand(`π`(s))
            val (s_next, reward, _) = a.sample()
            val _x = xFeature(s_next)

            val v = A_.T * (x - `γ` * _x)
            A_ -= (A_ * x) * v.T / (1.0 + v.T * x)
            b += reward * x
            s = s_next
            x = _x
        }
        episodeListener(episode)
    }
    vFunc.w `=` A_ * b
}