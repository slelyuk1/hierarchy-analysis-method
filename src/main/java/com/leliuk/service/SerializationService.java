package com.leliuk.service;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.*;

@Slf4j
@Getter
@Service
public class SerializationService {
    public <T extends Serializable> void serializeObject(T toSerialize, File toFile) throws IOException {
        try (FileOutputStream out = new FileOutputStream(toFile);
             ObjectOutputStream objectOutputStream = new ObjectOutputStream(out)) {
            objectOutputStream.writeObject(toSerialize);
        }
    }

    @SuppressWarnings("unchecked")
    public <T extends Serializable> T deserializeObject(File fromFile) throws IOException, ClassNotFoundException {
        try (FileInputStream in = new FileInputStream(fromFile);
             ObjectInputStream objectOutputStream = new ObjectInputStream(in)) {
            return (T) objectOutputStream.readObject();
        }
    }
}
