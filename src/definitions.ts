export interface VideoCapturePlugin {
  echo(options: { value: string }): Promise<{ value: string }>;
}
