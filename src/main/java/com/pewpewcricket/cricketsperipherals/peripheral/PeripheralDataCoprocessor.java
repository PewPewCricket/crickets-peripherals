package com.pewpewcricket.cricketsperipherals.peripheral;

import com.pewpewcricket.cricketsperipherals.blockentity.BlockEntityDataCoprocessor;
import com.pewpewcricket.cricketsperipherals.util.CryptoUtil;
import dan200.computercraft.api.lua.IArguments;
import dan200.computercraft.api.lua.LuaException;
import dan200.computercraft.api.lua.LuaFunction;
import dan200.computercraft.api.lua.MethodResult;
import dan200.computercraft.api.peripheral.IComputerAccess;
import dan200.computercraft.api.peripheral.IPeripheral;
import org.jetbrains.annotations.Nullable;
import org.jspecify.annotations.NonNull;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.io.ByteArrayOutputStream;
import java.nio.ByteBuffer;
import java.security.*;
import java.util.Base64;
import java.util.UUID;
import java.util.zip.CRC32;
import java.util.zip.DataFormatException;
import java.util.zip.Deflater;
import java.util.zip.Inflater;

public class PeripheralDataCoprocessor implements IPeripheral {
    private final BlockEntityDataCoprocessor be;

    public PeripheralDataCoprocessor(BlockEntityDataCoprocessor be) {
        this.be = be;
    }

    @Override
    public @NonNull String getType() {
        return "data_coprocessor";
    }

    @Override
    public boolean equals(@Nullable IPeripheral other) {
        return other instanceof PeripheralDataCoprocessor o && o.be == this.be;
    }

    @Override
    public void attach(@NonNull IComputerAccess computer) {
        be.attach(computer);
    }

    @Override
    public void detach(@NonNull IComputerAccess computer) {
        be.detach(computer);
    }

    @LuaFunction
    public MethodResult deflate(IArguments args) throws LuaException {
        ByteBuffer data = args.getBytes(0);
        byte[] bytes = new byte[data.remaining()];
        data.get(bytes);

        ByteArrayOutputStream output = new ByteArrayOutputStream();
        byte[] buffer = new byte[4096];

        Deflater deflater = new Deflater();
        deflater.setInput(bytes);
        deflater.finish();

        while (!deflater.finished()) {
            int count = deflater.deflate(buffer);
            output.write(buffer, 0, count);
        }

        deflater.end();
        return MethodResult.of(ByteBuffer.wrap(output.toByteArray()));
    }

    @LuaFunction
    public MethodResult inflate(IArguments args) throws LuaException {
        ByteBuffer data = args.getBytes(0);
        byte[] bytes = new byte[data.remaining()];
        data.get(bytes);

        Inflater inflater = new Inflater();
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        byte[] buffer = new byte[4096];

        inflater.setInput(bytes);

        try {
            while (!inflater.finished()) {
                int count = inflater.inflate(buffer);

                if (count == 0) {
                    if (inflater.needsInput()) break;
                }

                output.write(buffer, 0, count);
            }

        } catch (DataFormatException e) {
            throw new LuaException(e.getMessage());

        } finally {
            inflater.end();
        }

        return MethodResult.of(ByteBuffer.wrap(output.toByteArray()));
    }

    @LuaFunction
    public MethodResult sha256(IArguments args) throws LuaException {
        ByteBuffer data = args.getBytes(0);
        byte[] bytes = new byte[data.remaining()];
        data.get(bytes);

        MessageDigest digest;

        try {
            digest = MessageDigest.getInstance("SHA-256");
        } catch(NoSuchAlgorithmException e) {
            throw new LuaException(e.getMessage());
        }

        return MethodResult.of(ByteBuffer.wrap(digest.digest(bytes)));
    }

    @LuaFunction
    public MethodResult crc32(IArguments args) throws LuaException {
        ByteBuffer data = args.getBytes(0);
        byte[] bytes = new byte[data.remaining()];
        data.get(bytes);

        CRC32 crc = new CRC32();
        crc.update(bytes);

        return MethodResult.of(crc.getValue());
    }

    @LuaFunction
    public MethodResult key() throws LuaException {
        try {
            SecretKey key = CryptoUtil.generateKey();
            return MethodResult.of(ByteBuffer.wrap(key.getEncoded()));

        } catch (NoSuchAlgorithmException e) {
            throw new LuaException(e.getMessage());
        }
    }

    @LuaFunction
    public MethodResult encrypt(IArguments args) throws LuaException {
        ByteBuffer dataBuffer = args.getBytes(0);
        ByteBuffer keyBuffer = args.getBytes(1);

        byte[] data = new byte[dataBuffer.remaining()];
        dataBuffer.get(data);

        byte[] keyBytes = new byte[keyBuffer.remaining()];
        keyBuffer.get(keyBytes);

        try {
            SecretKey key = new SecretKeySpec(keyBytes, "AES");
            byte[] encrypted = CryptoUtil.encrypt(data, key);
            return MethodResult.of(ByteBuffer.wrap(encrypted));

        } catch (GeneralSecurityException e) {
            throw new LuaException(e.getMessage());
        }
    }

    @LuaFunction
    public MethodResult decrypt(IArguments args) throws LuaException {
        ByteBuffer dataBuffer = args.getBytes(0);
        ByteBuffer keyBuffer = args.getBytes(1);

        byte[] encrypted = new byte[dataBuffer.remaining()];
        dataBuffer.get(encrypted);

        byte[] keyBytes = new byte[keyBuffer.remaining()];
        keyBuffer.get(keyBytes);

        try {
            SecretKey key = new SecretKeySpec(keyBytes, "AES");
            byte[] decrypted = CryptoUtil.decrypt(encrypted, key);
            return MethodResult.of(ByteBuffer.wrap(decrypted));

        } catch (GeneralSecurityException e) {
            throw new LuaException(e.getMessage());
        }
    }

    @LuaFunction
    public MethodResult encode64(IArguments args) throws LuaException {
        ByteBuffer data = args.getBytes(0);
        byte[] bytes = new byte[data.remaining()];
        data.get(bytes);

        return MethodResult.of(Base64.getEncoder().encodeToString(bytes));
    }

    @LuaFunction
    public MethodResult decode64(IArguments args) throws LuaException {
        String encoded = args.getString(0);

        try {
            return MethodResult.of(ByteBuffer.wrap(Base64.getDecoder().decode(encoded)));
        } catch (IllegalArgumentException e) {
            throw new LuaException(e.getMessage());
        }
    }

    @LuaFunction
    public MethodResult uuid() {
        return MethodResult.of(UUID.randomUUID().toString());
    }
}
