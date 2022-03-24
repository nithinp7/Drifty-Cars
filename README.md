# Drifty-Cars

Disclaimer: I built this in high-school (Spring 2017)

A top-down, procedural, car-chase game. Drifty Cars was built in Java using the following libraries:
- Processing - A wrapper around JOGL (which itself is a Java binding of OpenGL).
- Beads - For sounds.
- Box2D - For physics.

Some notable hand-built features:
- PID-based AI for both road traffic as well as police.
  - AI can follow roads, avoid obstacles, anticipate the motion of other vehicles, and even reverse out when stuck. 
  - The AI path-finding is robust due to PID controls and can recover quickly when crashed into.
  - The police AI effectively avoid hitting each other or traffic, while attempting to ram the player.
- A simple procedural system to place roads and buildings. The procedural roads auto-generate target paths for the road traffic AI. The world center has to seamlessly keep moving to keep the player near the center. A seamless "conveyor-belt" system was implemented successfully to do this.
- Many visual features (mostly naively implemented)
  - Particle system for police siren lights, kicked up dust, and explosions.
  - A render-target to track tire marks on dirt, that works with the procedural "conveyor-belt" movement.
  - Simple directional shadow mapping for the buildings.
  - Extremely simple, arcade-style UI.

YouTube videos of the project:
- Early stages: https://youtu.be/CVuJM_3zKpw
- Final state: https://youtu.be/yUA8AIgW-tk
