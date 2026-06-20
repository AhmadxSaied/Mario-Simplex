import { Component, Output, EventEmitter } from '@angular/core';

@Component({
  selector: 'app-splash-screen',
  standalone: true,
  template: `
    <div class="splash-container" (click)="startGame()">
      <div class="content">
        <h1 class="title font-mario">MARIO SIMPLEX</h1>

        <img src="assets/mario_jump_animation.gif" alt="Mario" class="mario-img" />

        <p class="start-text font-mario">CLICK TO START</p>
      </div>
    </div>
  `,
  styles: [
    `
      .splash-container {
        position: fixed;
        top: 0;
        left: 0;
        width: 100vw;
        height: 100vh;
        background-color: #020617; /* matches slate-950 */
        display: flex;
        justify-content: center;
        align-items: center;
        z-index: 99999;
        cursor: pointer;
        font-family: 'Courier New', Courier, monospace; /* Fallback until your Mario font loads */
      }

      .content {
        text-align: center;
        display: flex;
        flex-direction: column;
        align-items: center;
      }

      .title {
        font-size: 4rem;
        color: transparent;
        background-clip: text;
        -webkit-background-clip: text;
        background-image: linear-gradient(to right, #ef4444, #f87171); /* red-500 to red-400 */
        margin-bottom: 2rem;
        letter-spacing: 0.1em;
        filter: drop-shadow(0 4px 0 rgba(200, 200, 200, 1));
      }

      .mario-img {
        width: 360px;
        height: 360px;
        margin-bottom: 3rem;
        background: transparent;
      }

      .start-text {
        color: #fff;
        font-size: 1.5rem;
        letter-spacing: 0.2em;
        animation: blink 1s step-end infinite;
      }

      @keyframes blink {
        0%,
        100% {
          opacity: 1;
        }
        50% {
          opacity: 0;
        }
      }
    `,
  ],
})
export class SplashScreen {
  @Output() start = new EventEmitter<void>();

  startGame() {
    this.start.emit();
  }
}
