package de.heisluft.capi.resources;

import javax.annotation.Nonnull;
import java.io.InputStream;

public class DefaultAssetPack implements IAssetPack {

	private static final ClassLoader CLASS_LOADER = DefaultAssetPack.class.getClassLoader();

	@Nonnull
	@Override
	public InputStream getInputStream(ResourceLocation location) throws MissingResourceException{
		String name = "assets/" + location.getResourceDomain() + "/" + location.getLocation();
		InputStream inputStream = CLASS_LOADER.getResourceAsStream(name);
		if(inputStream == null) throw new MissingResourceException(location);
		return inputStream;
	}
}
