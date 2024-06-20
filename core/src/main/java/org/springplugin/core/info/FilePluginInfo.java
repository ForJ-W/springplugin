package org.springplugin.core.info;

import com.google.gson.Gson;
import org.apache.commons.io.FileUtils;
import org.springplugin.core.classloader.SpringPluginClassLoader;
import org.springplugin.core.exception.PluginException;
import org.springplugin.core.util.StringUtils;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Objects;
import java.util.Optional;

/**
 *  文件插件信息
 *
 * @author afěi
 * @version 1.0.0
 */
public record FilePluginInfo(String name, String mainClassName) implements PluginInfo {

    private static final Gson GSON = new Gson();

    public FilePluginInfo {
        try {
            writeInfo(this);
        } catch (IOException e) {
            throw new PluginException("Write file fail: '.info'", e);
        }
    }

    @Override
    public String mainClassName() {
        if (Objects.isNull(this.name)) {
            return null;
        }
        final File pluginPath = new File(SpringPluginClassLoader.LOAD_PATH, name);
        final PluginException pe = new PluginException(String.format("Can not find plugin '%s'", name));
        try {
            final File infoFile = new File(pluginPath, ".info");
            if (infoFile.exists()) {
                final String fileContent = FileUtils.readFileToString(new File(pluginPath, ".info"), StandardCharsets.UTF_8);
                return Optional.ofNullable(GSON.fromJson(fileContent, PluginInfo.class)).orElseThrow(() -> pe).mainClassName();
            }
        } catch (IOException e) {
            throw new PluginException(String.format("Can not find plugin '%s'", name), e);
        }
        return null;
    }

    @Override
    public Class<?> mainClass() throws ClassNotFoundException {
        return StringUtils.isNotBlank(this.mainClassName)
                ? SpringPluginClassLoader.getInstance(name()).forName(mainClassName())
                : PluginInfo.super.mainClass();
    }


    /**
     * 写入一份.info的描述文件
     *
     * @param info 插件信息
     *
     * @throws IOException 写文件时可能抛出的IO异常
     */
    private void writeInfo(PluginInfo info) throws IOException {
        final String name = info.name();
        final String mainClassName = info.mainClassName();
        if (StringUtils.isNotBlank(mainClassName)) {
            final File pluginPath = new File(SpringPluginClassLoader.LOAD_PATH, name);
            FileUtils.writeStringToFile(new File(pluginPath, ".info"), GSON.toJson(info), StandardCharsets.UTF_8);
        }
    }
}
