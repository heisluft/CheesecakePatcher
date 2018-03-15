package de.heisluft.capi.resources;

import de.heisluft.annotation.FieldsAreNonNullByDefault;
import de.heisluft.annotation.MethodsReturnNonNullByDefault;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Objects;
import java.util.regex.Pattern;

/**
 * A ResourceLocation describes a path to a resource.
 * The resourceDomain should be your mod name, otherwise it will default to 'cheesecake'.
 *
 * @author Heisluft
 */
@FieldsAreNonNullByDefault
@ParametersAreNonnullByDefault
@MethodsReturnNonNullByDefault
public class ResourceLocation implements Comparable<ResourceLocation> {

	/** The RegEx split pattern */
	private static final Pattern SPLIT_PATTERN = Pattern.compile(":");

	/** The resource domain */
	private final String resourceDomain;
	/** The resource path name */
	private final String location;

	/**
	 * Constructs a new ResourceLocation by a fully qualified resource name, e.g. "cheesecake:this/is/a/test.png".
	 * If no resourceDomain is given, it will default to "cheesecake".
	 *
	 * @param name the name of the Resource
	 */
	public ResourceLocation(String name) {
		this(null, name);
	}

	@Override
	public String toString() {
		return resourceDomain + ":" + location;
	}

	/**
	 * Constructs a ResourceLocation. There a several ways to do this:
	 * <ul>
	 *     <li>By setting resourceDomain and location: {@code new ResourceLocation("myDomain", "path/to/resour.ce")}</li>
	 *     <li>By setting resourceDomain within location: {@code new ResourceLocation(null, "myDomain:path/to/resour.ce")},
	 *     see also {@link #ResourceLocation(String)}</li>
	 *     <li>By not setting resourceDomain: {@code new ResourceLocation(null, "path/to/resour.ce")}.
	 *     <em>Note: the domain will be "cheesecake" in that case</em></li>
	 * </ul>
	 *
	 * @param resourceDomain The nullable domain. If not set and not provided from within location,
	 *                       it will default to "cheesecake"
	 * @param location The location path.
	 */
	public ResourceLocation(@Nullable String resourceDomain, String location) {
		if(resourceDomain != null && !resourceDomain.isEmpty()) {
			this.location = location;
			this.resourceDomain = resourceDomain;
		}
		else if(location.contains(":")) {
			String[] split = SPLIT_PATTERN.split(location, 2);
			this.resourceDomain = split[0];
			this.location = split[1];
		}
		else {
			this.resourceDomain = "cheesecake";
			this.location = location;
		}
	}

	public boolean equals(Object anObject) {
		if (this == anObject) return true;
		else if(!(anObject instanceof ResourceLocation)) return false;
		else {
			ResourceLocation loc = (ResourceLocation)anObject;
			return resourceDomain.equals(loc.resourceDomain) && this.location.equals(loc.location);
		}
	}

	/**
	 * Gets the path name described by this resource location
	 *
	 * @return The location path name
	 */
	public String getLocation() {
		return location;
	}

	/**
	 * Gets the resource domain
	 *
	 * @return The resource domain's name
	 */
	public String getResourceDomain() {
		return resourceDomain;
	}

	public int compareTo(ResourceLocation other) {
		int i = resourceDomain.compareTo(other.resourceDomain);
		if (i == 0) i = location.compareTo(other.location);
		return i;
	}

	@Override
	public int hashCode() {
		return Objects.hash(resourceDomain, location);
	}
}
