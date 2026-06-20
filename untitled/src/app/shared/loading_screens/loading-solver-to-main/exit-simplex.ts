import {
  Component,
  ElementRef,
  ViewChild,
  AfterViewInit,
  OnDestroy,
  HostListener,
  Output,
  EventEmitter,
} from '@angular/core';
import * as THREE from 'three';
import { GLTFLoader } from 'three/examples/jsm/loaders/GLTFLoader.js';

@Component({
  selector: 'app-exit-simplex',
  imports: [],
  standalone: true,
  templateUrl: './exit-simplex.html',
  styleUrl: './exit-simplex.css',
})
export class ExitSimplex implements AfterViewInit, OnDestroy {
  @Output() transitionFinished = new EventEmitter<void>();
  private startTime: number = 0;

  @ViewChild('rendererContainer', { static: true }) rendererContainer!: ElementRef<HTMLDivElement>;

  private renderer!: THREE.WebGLRenderer;
  private scene!: THREE.Scene;
  private camera!: THREE.PerspectiveCamera;
  private animationFrameId!: number;
  private marioStand!: THREE.Object3D | undefined;

  // Variables to track vertical coordinates
  private startStandY: number = 0;
  private pipeTopOfLipY: number = 0;

  ngAfterViewInit(): void {
    this.initThreeJs();
  }

  private initThreeJs(): void {
    // 1. SETUP THE SCENE (No background color, so it remains clear!)
    this.scene = new THREE.Scene();

    // 2. SETUP THE CAMERA (Angled down to look at the vertical pipe)
    this.camera = new THREE.PerspectiveCamera(
      45,
      window.innerWidth / window.innerHeight,
      0.1,
      1000,
    );
    this.camera.position.set(0, 25, 90);
    this.scene.background = new THREE.Color(0x5c94fc);
    // 3. SETUP THE RENDERER (Notice alpha: true to make the canvas transparent)
    this.renderer = new THREE.WebGLRenderer({ antialias: true, alpha: true });
    this.renderer.setSize(window.innerWidth, window.innerHeight);
    this.renderer.outputColorSpace = THREE.SRGBColorSpace;
    this.rendererContainer.nativeElement.appendChild(this.renderer.domElement);

    // 4. ADD LIGHTING
    const ambientLight = new THREE.AmbientLight(0xffffff, 0.6);
    this.scene.add(ambientLight);

    const directionalLight = new THREE.DirectionalLight(0xffffff, 0.8);
    directionalLight.position.set(10, 20, 10);
    this.scene.add(directionalLight);

    // -------------------------------------------------------------
    // 5. LOAD THE BLENDER FILE
    // -------------------------------------------------------------
    const loader = new GLTFLoader();

    loader.load(
      'assets/mario_model.glb',
      (gltf) => {
        const marioDiorama = gltf.scene;
        this.scene.add(marioDiorama);
        this.renderer.render(this.scene, this.camera);

        // Hide the background wall just like in the loading screen
        const background = marioDiorama.getObjectByName('Shape1_cor025_cinza0256_0');
        const walking = marioDiorama.getObjectByName('walking');
        if (background && walking) {
          background.visible = false;
          walking.visible = false;
        }

        // Grab Mario and the vertical pipe
        this.marioStand = marioDiorama.getObjectByName('Shape1_Mario');
        const pipeLip = marioDiorama.getObjectByName('Tube001_Sup_Tube_0');

        if (this.marioStand && pipeLip) {
          // Detach Mario to use world coordinates
          this.scene.attach(this.marioStand);

          // 1. Find the exact vertical height of the pipe's top Lip
          const lipPos = new THREE.Vector3();
          pipeLip.getWorldPosition(lipPos);
          this.pipeTopOfLipY = lipPos.y;
          this.marioStand.rotation.y =  Math.PI
          this.marioStand.position.x = 92;
          this.marioStand.position.z = 0;

          // 3. Bury Mario deep inside the pipe to start
          this.marioStand.position.y = this.pipeTopOfLipY - 35;
          this.startStandY = this.marioStand.position.y;
        }

        this.startAnimation();
      },
      undefined,
      (error) => {
        console.error('Error loading the Mario exit model:', error);
      },
    );
  }

  private startAnimation(): void {
    this.startTime = Date.now();

    const loop = () => {
      this.animationFrameId = requestAnimationFrame(loop);

      const time = Date.now();
      const progress = time - this.startTime;

      if (progress >= 3000) {
        cancelAnimationFrame(this.animationFrameId);
        this.transitionFinished.emit();
        return;
      }

      if (this.marioStand) {
        // --- 🛠️ TWEAK HIS MAX HEIGHT HERE 🛠️ ---
        // If he goes too high, use a negative number (like -2, -4, or -6) to pull him down.
        // If he doesn't come up far enough, use a positive number.
        const heightAdjustment = -7.8;

        // Calculate the new perfect peak height
        const peakHeight = this.pipeTopOfLipY + heightAdjustment;
        const distanceToRise = peakHeight - this.startStandY;

        // --- PHASE 1: RISING FROM PIPE (0s → 1.2s) ---
        if (progress < 1200) {
          const risePercentage = progress / 1200;
          this.marioStand.position.y = this.startStandY + distanceToRise * risePercentage;
          this.marioStand.visible = true;

          // --- PHASE 2: PAUSE AT TOP (1.2s → 1.8s) ---
        } else if (progress < 1800) {
          this.marioStand.position.y = peakHeight; // Locked to your adjusted height
          this.marioStand.visible = true;

          // --- PHASE 3: SINKING BACK DOWN (1.8s → 3.0s) ---
        } else {
          const sinkPercentage = (progress - 1800) / 1200;
          this.marioStand.position.y = peakHeight - distanceToRise * sinkPercentage;

          if (sinkPercentage >= 1) {
            this.marioStand.visible = false;
          } else {
            this.marioStand.visible = true;
          }
        }
      }

      this.renderer.render(this.scene, this.camera);
    };

    loop();
  }

  @HostListener('window:resize')
  onWindowResize(): void {
    if (this.camera && this.renderer) {
      this.camera.aspect = window.innerWidth / window.innerHeight;
      this.camera.updateProjectionMatrix();
      this.renderer.setSize(window.innerWidth, window.innerHeight);
      this.renderer.render(this.scene, this.camera);
    }
  }

  ngOnDestroy(): void {
    if (this.renderer) {
      this.renderer.dispose();
    }
  }
}
