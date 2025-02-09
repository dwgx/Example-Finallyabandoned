package com.example.mod.managers;

import com.diaoling.schema.config.ConfigSchema;
import com.diaoling.schema.file.FileSchema;
import com.example.information.AppInfo;
import com.example.mod.features.ClientSettings;
import com.example.mod.features.module.AbstractModule;
import com.example.mod.file.AbstractFile;
import com.example.mod.file.impl.FileClientConfig;
import com.example.mod.file.impl.FileModuleConfig;
import com.example.mod.file.impl.FileSettingsConfig;
import com.example.utils.FileUtils;
import com.example.utils.interfaces.Initializable;
import com.example.utils.interfaces.Manageable;
import com.example.utils.pattern.Singleton;
import com.google.protobuf.GeneratedMessage;
import com.google.protobuf.InvalidProtocolBufferException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

import static com.example.mod.client.GameAccessor.mc;

public class FileManager implements Initializable, Manageable<AbstractFile<?, ?>> {
    private static final Logger LOGGER = LoggerFactory.getLogger(FileManager.class);

    private final Set<AbstractFile<?, ?>> files = new CopyOnWriteArraySet<>();

    private final Path rootDir = Paths.get(mc.runDirectory.getAbsolutePath(), AppInfo.NAME);

    @Override
    public boolean init() {
        File rootDirFile = this.rootDir.toFile();
        if (!rootDirFile.exists()) {
            if (rootDirFile.mkdir()) {
                return true;
            }
        }

        List<File> foundFiles = new ArrayList<>();
        FileUtils.traverseDirectory(rootDirFile, foundFiles);

        for (File file : foundFiles) {
            try {
                String fileName = file.getName();
                byte[] fileBytes = FileUtils.readFile(file);

                FileSchema.BaseFile baseFile = FileSchema.BaseFile.parseFrom(fileBytes);
                byte[] bytes = baseFile.getContext().toByteArray();

                FileSchema.FileType fileType = baseFile.getType();

                final Path path = Path.of(fileName);

                switch (fileType) {
                    case UNKNOWN_FILE, UNRECOGNIZED -> {
                    }
                    case SETTINGS_CONFIG_FILE -> {
                        ConfigSchema.SettingsConfig config = ConfigSchema.SettingsConfig.parseFrom(bytes);

                        AbstractFile<ConfigSchema.SettingsConfig, ?> parsedFile = new FileSettingsConfig(
                                path,
                                null,
                                fileType,
                                baseFile,
                                config
                        );
                        parsedFile.read();
                        this.add(parsedFile);
                    }
                    case MODULE_CONFIG_FILE -> {
                        ConfigSchema.ModuleConfig config = ConfigSchema.ModuleConfig.parseFrom(bytes);
                        AbstractModule module = ModuleManager.getInstance().getModuleByName(config.getModuleName());

                        if (module != null) {
                            // TODO: fix wrong path
                            AbstractFile<ConfigSchema.ModuleConfig, AbstractModule> parsedFile = new FileModuleConfig(
                                    // path,
                                    Path.of("modules", fileName),
                                    module,
                                    fileType,
                                    baseFile,
                                    config
                            );

                            parsedFile.read();
                            this.add(parsedFile);
                        }
                        /*
                        System.out.println(
                                parsedFile.getPath() + "- \n" + parsedFile.getData().getConfig().getSettingsMap()
                        );
                         */
                    }
                    case CLIENT_CONFIG_FILE -> {
                        ConfigSchema.ClientConfig config = ConfigSchema.ClientConfig.parseFrom(bytes);

                        AbstractFile<ConfigSchema.ClientConfig, ClientSettings> parsedFile = new FileClientConfig(
                                path,
                                ClientSettings.getInstance(),
                                fileType,
                                baseFile,
                                config
                        );
                        parsedFile.read();
                        this.add(parsedFile);
                    }
                }
            } catch (InvalidProtocolBufferException e) {
                LOGGER.error("Invalid file: {}", file.getAbsolutePath());
            } catch (IOException e) {
                LOGGER.error("Failed to read or deserialize file: {}", file.getAbsolutePath());
                e.printStackTrace();
            }
        }

        return false;
    }

    @Override
    public boolean destroy() {
        this.files.forEach(AbstractFile::save);
        this.saveAll();

        return true;
    }

    @Override
    public boolean add(AbstractFile<?, ?> element) {
        for (AbstractFile<?, ?> file : this.files) {
            if (file.getFileType() == element.getFileType() && file.getPath().equals(element.getPath())) {
                // return false;

                // TODO: refactor add
                this.remove(file);
            }
        }

        return this.files.add(element);
    }

    @Override
    public boolean remove(AbstractFile<?, ?> element) {
        return this.files.remove(element);
    }

    @Override
    public List<AbstractFile<?, ?>> items() {
        return this.files.stream().toList();
    }

    public void saveAll() {
        this.files.forEach(
                abstractFile -> {
                    if (abstractFile.getData() instanceof GeneratedMessage generatedMessage) {
                        abstractFile.save();

                        FileSchema.BaseFile baseFile = abstractFile.getBaseFile();

                        baseFile = baseFile.toBuilder()
                                .putAllMetadata(baseFile.getMetadataMap())
                                .setType(abstractFile.getFileType())
                                .setCreatedTime(baseFile.getCreatedTime() == 0 ? System.currentTimeMillis() : baseFile.getCreatedTime())
                                .setModifiedTime(System.currentTimeMillis())
                                .setContext(generatedMessage.toByteString())
                                .build();

                        byte[] bytes = baseFile.toByteArray();

                        try {
                            FileUtils.writeFile(bytes, Path.of(this.rootDir.toString(), abstractFile.getPath().toString()));
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
        );
    }

    public Path getRootDir() {
        return rootDir;
    }

    public Set<AbstractFile<?, ?>> getFiles() {
        return files;
    }

    public static FileManager getInstance() {
        return Singleton.getInstance(FileManager.class);
    }
}
