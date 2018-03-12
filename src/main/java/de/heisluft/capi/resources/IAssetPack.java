package de.heisluft.capi.resources;

import javax.annotation.Nonnull;
import java.io.InputStream;

public interface IAssetPack {

	@Nonnull
	InputStream getInputStream(ResourceLocation location) throws MissingResourceException;
}
