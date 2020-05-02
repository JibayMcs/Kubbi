package fr.leviathanstudio.engine.graph.particles;

import fr.leviathanstudio.engine.items.GameItem;

import java.util.List;

public interface IParticleEmitter {

    void cleanup();

    Particle getBaseParticle();

    List<GameItem> getParticles();
}
