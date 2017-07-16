package wumo.rl.model.impl;

import wumo.rl.model.MDP;
import wumo.rl.util.tuple3;

import java.util.Iterator;

public class D3DPossibleSet implements MDP.PossibleSet {
    public MDP.Possible[][][] raw;
    
    @Override public MDP.Possible get(Object index) {
        tuple3 idx = (tuple3) index;
        return raw[idx._1][idx._2][idx._3];
    }
    
    @Override public void set(Object index, MDP.Possible s) {
        tuple3 idx = (tuple3) index;
        raw[idx._1][idx._2][idx._3] = s;
    }
    
    @Override public Iterator<MDP.Possible> iterator() {
        return new Iterator<MDP.Possible>() {
            int a = 0, b = 0, c = 0;
            
            @Override public boolean hasNext() {
                return a < raw.length && b < raw[a].length && c < raw[a][b].length;
            }
            
            @Override public MDP.Possible next() {
                int _a = a, _b = b, _c = c;
                c++;
                if (c >= raw[a][b].length) {
                    b++; c = 0;
                    if (b >= raw[a].length) {
                        a++; b = 0;
                    }
                }
                return raw[_a][_b][_c];
            }
        };
    }
}