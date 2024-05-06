import { WebPlugin } from '@capacitor/core';

import type { VideoCapturePlugin } from './definitions';

export class VideoCaptureWeb extends WebPlugin implements VideoCapturePlugin {
  async echo(options: { value: string }): Promise<{ value: string }> {
    console.log('ECHO', options);
    return options;
  }
}
