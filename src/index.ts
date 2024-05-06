import { registerPlugin } from '@capacitor/core';

import type { VideoCapturePlugin } from './definitions';

const VideoCapture = registerPlugin<VideoCapturePlugin>('VideoCapture', {
  web: () => import('./web').then(m => new m.VideoCaptureWeb()),
});

export * from './definitions';
export { VideoCapture };
