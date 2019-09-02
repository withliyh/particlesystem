package com.nd.particlesystem;

import com.nd.particlesystem.core.SizeRotAlphaSimulator;

import org.junit.Test;

import static org.junit.Assert.*;
import static com.nd.particlesystem.core.BaseSimulate.Var;
import static com.nd.particlesystem.core.BaseSimulate.Range;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {
    @Test
    public void addition_isCorrect() {
        assertEquals(4, 2 + 2);
    }

    @Test
    public void sizeRotAlphaSimulatorTest() {
        SizeRotAlphaSimulator.SizeRotAlphaConfig config = new SizeRotAlphaSimulator.SizeRotAlphaConfig();
        config.macParticleCount = 10000;
        config.duration = 10; // sec
        config.rate = 1000; // count/sec
        config.life = new Var(5, 3); //sec
        config.size = new Range(new Var(100, 10), new Var(10, 10));
        config.R = new Range(new Var(1, 0), new Var(1, 0));
        config.G = new Range(new Var(0, 0), new Var(0, 0));
        config.B = new Range(new Var(0, 0), new Var(0, 0));
        config.A = new Range(new Var(1, 0), new Var(1, 0));
        config.speed = new Var(100, 100);
        config.angle = new Var(0, 90);
        config.rotation = new Range(new Var(0, 0), new Var(0, 360));
        config.x = new Var(500, 0);
        config.y = new Var(500, 0);

        SizeRotAlphaSimulator simulator = new SizeRotAlphaSimulator(config);

        simulator.initialize();
        simulator.start();

        //sec
        simulator.simulate(2);
    }
}