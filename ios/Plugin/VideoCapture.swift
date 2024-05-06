import Foundation
import Capacitor
import AVFoundation

@objc public class VideoCapture: CAPPlugin {

    var captureSession: AVCaptureSession?
    var videoOutput: AVCaptureMovieFileOutput?
    var videoFileURL: URL?

    @objc func startRecording(_ call: CAPPluginCall) {
        let duration = call.getInt("duration") ?? 30
        let quality = call.getString("quality") ?? AVCaptureSession.Preset.high.rawValue

        captureSession = AVCaptureSession()
        captureSession?.beginConfiguration()

        guard let camera = AVCaptureDevice.default(for: .video) else {
            call.reject("No se pudo acceder a la cámara.")
            return
        }

        do {
            let videoInput = try AVCaptureDeviceInput(device: camera)
            if captureSession!.canAddInput(videoInput) {
                captureSession!.addInput(videoInput)
            }
        } catch {
            call.reject("Error al configurar la entrada de video.")
            return
        }

        videoOutput = AVCaptureMovieFileOutput()
        if captureSession!.canAddOutput(videoOutput!) {
            captureSession!.addOutput(videoOutput!)
        }

        captureSession?.sessionPreset = AVCaptureSession.Preset(rawValue: quality)

        let documentsPath = NSSearchPathForDirectoriesInDomains(.documentDirectory, .userDomainMask, true)[0]
        let outputURL = URL(fileURLWithPath: documentsPath).appendingPathComponent("video.mp4")
        videoOutput?.startRecording(to: outputURL, recordingDelegate: self)

        DispatchQueue.main.asyncAfter(deadline: .now() + .seconds(duration)) {
            self.stopRecording(call)
        }

        call.resolve()
    }

    @objc func stopRecording(_ call: CAPPluginCall) {
        videoOutput?.stopRecording()
        call.resolve()
    }
}

extension VideoRecorder: AVCaptureFileOutputRecordingDelegate {
    public func fileOutput(_ output: AVCaptureFileOutput, didFinishRecordingTo outputFileURL: URL, from connections: [AVCaptureConnection], error: Error?) {
        videoFileURL = outputFileURL
    }
}
