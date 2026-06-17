package com.pewpewcricket.cricketsperipherals.util;

import javax.sound.sampled.*;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

/*
 * Ignore this shit I'm trying to add built in audio encoding and decoding to the data coprocessor, but I am very stupid.
 */
public final class AudioUtil {
    public static byte[] decodeToPCM8(byte[] audioBytes) throws UnsupportedAudioFileException, IOException {
        InputStream audioByteStream = new ByteArrayInputStream(audioBytes);
        AudioInputStream audioStream = AudioSystem.getAudioInputStream(audioByteStream);

        AudioFormat baseFormat = audioStream.getFormat();
        AudioFormat targetFormat = new AudioFormat(
                AudioFormat.Encoding.PCM_SIGNED,
                48000.0F,
                8,
                1,
                1,
                48000.0F,
                false
        );

        if (!AudioSystem.isConversionSupported(targetFormat, baseFormat)) {
            throw new UnsupportedAudioFileException(
                    "Conversion from " + baseFormat + " to " + targetFormat + "is not supported."
            );
        }

        AudioInputStream targetStream = AudioSystem.getAudioInputStream(targetFormat, audioStream);
        ByteArrayOutputStream targetBuffer = new ByteArrayOutputStream();

        int frameSize = targetStream.getFormat().getFrameSize();
        byte[] targetData = new byte[frameSize * 1024];

        int bytesRead;
        while ((bytesRead = targetStream.read(targetData, 0, targetData.length)) != -1) {
            targetBuffer.write(targetData, 0, bytesRead);
        }

        return targetBuffer.toByteArray();
    }
}
