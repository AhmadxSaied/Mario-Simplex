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
  selector: 'app-loading-simplex',
  imports: [],
  templateUrl: './loading-simplex.html',
  styleUrl: './loading-simplex.css',
})
export class LoadingSimplex implements AfterViewInit, OnDestroy {
  @Output() animationFinished = new EventEmitter<void>();
  private startTime:number = 0;

  @ViewChild('rendererContainer', { static: true }) rendererContainer!: ElementRef<HTMLDivElement>;

  private renderer!: THREE.WebGLRenderer;
  private scene!: THREE.Scene;
  private camera!: THREE.PerspectiveCamera;
  private animationFrameId!: number;
  private marioStand!: THREE.Object3D | undefined;
  private marioWalk!: THREE.Object3D | undefined;
  private startStandY: number = 0;
  private startWalkY: number = 0;
  private standWalkOffset: number = 0;
  private startStandX: number = 0;
  ngAfterViewInit(): void {
    this.initThreeJs();
  }

  private initThreeJs(): void {
    // 1. SETUP THE SCENE
    this.scene = new THREE.Scene();
    this.scene.background = new THREE.Color(0x5c94fc);

    // 2. SETUP THE CAMERA
    this.camera = new THREE.PerspectiveCamera(
      45,
      window.innerWidth / window.innerHeight,
      0.1,
      1000,
    );
    this.camera.position.set(2, 25, 90);

    // 3. SETUP THE RENDERER
    this.renderer = new THREE.WebGLRenderer({ antialias: true });
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
    // 5. LOAD YOUR BLENDER FILE
    // -------------------------------------------------------------
    const loader = new GLTFLoader();

    // Point the loader to the assets folder where you put the file
    loader.load(
      'assets/mario_model.glb',
      (gltf) => {
        const marioDiorama = gltf.scene;
        this.scene.add(marioDiorama);
        this.renderer.render(this.scene, this.camera);
        this.marioStand = marioDiorama.getObjectByName('Shape1_Mario');
        this.marioWalk = marioDiorama.getObjectByName('walking');
        const background = marioDiorama.getObjectByName('Shape1_cor025_cinza0256_0');
        if (background) {
          background.visible = false;
        }
        if (this.marioStand && this.marioWalk) {
          // 1. Capture marioStand's WORLD position before detaching
          const standWorldPos = new THREE.Vector3();
          this.marioStand.getWorldPosition(standWorldPos);

          // 2. Detach it from Object_20 and re-add it directly to the scene
          this.scene.attach(this.marioStand);
          this.scene.attach(this.marioWalk);
          this.startWalkY = this.marioWalk.position.y;
          this.startStandY = this.marioStand.position.y;
          this.startStandX = this.marioStand.position.x;
          this.standWalkOffset = this.marioWalk.position.x - this.marioStand.position.x;
        }

        this.startAnimation();
      },
      undefined,
      (error) => {
        console.error('Error loading the Mario model:', error);
      },
    );
  }
  private startAnimation(): void {
    this.startTime = Date.now();
    const loop = () => {
      this.animationFrameId = requestAnimationFrame(loop);

      const time = Date.now();
      const progress = time  - this.startTime;
      if(progress >= 2500){
        cancelAnimationFrame(this.animationFrameId);
        this.animationFinished.emit();
        return;
      }
      if (this.marioStand && this.marioWalk) {
        const distanceToPipe = 23;
        const walkSpeed = 150;

        if (progress < 1200) {
          const walkPercentage = progress / 1200;
          const moveAmount = distanceToPipe * walkPercentage;

          this.marioStand.position.x = this.startStandX + moveAmount;
          this.marioWalk.position.x = this.marioStand.position.x + this.standWalkOffset;

          if (Math.floor(time / walkSpeed) % 2 === 0) {
            this.marioStand.visible = true;
            this.marioWalk.visible = false;
          } else {
            this.marioStand.visible = false;
            this.marioWalk.visible = true;
          }
        } else if (progress < 1400) {
          this.marioStand.position.x = this.startStandX + distanceToPipe;
          this.marioWalk.position.x = this.marioStand.position.x + this.standWalkOffset;

          this.marioStand.visible = true;
          this.marioWalk.visible = false;

        } else if (progress < 2000) {
          const enterProgress = (progress - 1400) / 600; // 0 → 1
          const enterAmount = 8 * enterProgress; // Slides right into pipe, adjust 6 to taste

          this.marioStand.position.x = this.startStandX + distanceToPipe + enterAmount;
          this.marioWalk.position.x = this.marioStand.position.x + this.standWalkOffset;

          this.marioStand.position.y = this.startStandY;
          this.marioWalk.position.y = this.startWalkY;

          if (Math.floor(time / walkSpeed) % 2 === 0) {
            this.marioStand.visible = true;
            this.marioWalk.visible = false;
          } else {
            this.marioStand.visible = false;
            this.marioWalk.visible = true;
          }

        } else {
          this.marioStand.visible = false;
          this.marioWalk.visible = false;

          // Reset marioStand to start
          this.marioStand.position.x = this.startStandX;
          this.marioStand.position.y = this.startStandY;

          // Reset marioWalk using the SAME formula used during animation
          this.marioWalk.position.x = this.startStandX + this.standWalkOffset;
          this.marioWalk.position.y = this.startWalkY;
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

      // Re-render when resized
      this.renderer.render(this.scene, this.camera);
    }
  }

  ngOnDestroy(): void {
    if (this.renderer) {
      this.renderer.dispose();
    }
  }
}
