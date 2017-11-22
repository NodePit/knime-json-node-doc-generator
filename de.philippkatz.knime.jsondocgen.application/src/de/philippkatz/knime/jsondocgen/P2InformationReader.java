package de.philippkatz.knime.jsondocgen;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import org.eclipse.equinox.p2.core.IProvisioningAgent;
import org.eclipse.equinox.p2.core.IProvisioningAgentProvider;
import org.eclipse.equinox.p2.core.ProvisionException;
import org.eclipse.equinox.p2.engine.IProfile;
import org.eclipse.equinox.p2.engine.IProfileRegistry;
import org.eclipse.equinox.p2.metadata.IInstallableUnit;
import org.eclipse.equinox.p2.query.IQueryResult;
import org.eclipse.equinox.p2.query.QueryUtil;
import org.osgi.framework.ServiceReference;

import de.philippkatz.knime.jsondocgen.docs.FeatureDoc;
import de.philippkatz.knime.jsondocgen.docs.FeatureDoc.FeatureDocBuilder;

/**
 * Uses P2 to read metadata about features (and plugins as well).
 * 
 * @author Philipp Katz
 */
public final class P2InformationReader {

	public static List<FeatureDoc> readFeatureInfo() {

		ServiceReference<IProvisioningAgentProvider> serviceReference = Activator.getContext()
				.getServiceReference(IProvisioningAgentProvider.class);
		if (serviceReference == null) {
			throw new IllegalStateException("ServiceReference for IProvisioningAgentProvider is NULL");
		}

		IProvisioningAgent provisioningAgent = null;

		try {

			IProvisioningAgentProvider provider = Activator.getContext().getService(serviceReference);
			if (provider == null) {
				throw new IllegalStateException("IProvisioningAgentProvider is NULL");
			}

			provisioningAgent = provider.createAgent(null);
			IProfileRegistry profileRegistry = (IProfileRegistry) provisioningAgent
					.getService(IProfileRegistry.SERVICE_NAME);

			IProfile profile = profileRegistry.getProfile(IProfileRegistry.SELF);
			if (profile == null) {
				System.out.println(String.format(
						"Couldn't get profile '%s' -- this should only happen when launching from Eclipse. "
								+ "Install this application into KNIME and run via command line to avoid this.",
						IProfileRegistry.SELF));
				return Collections.emptyList();
			}

			IQueryResult<IInstallableUnit> allIUs = profile.query(QueryUtil.createIUAnyQuery(), null);

			return StreamSupport.stream(allIUs.spliterator(), false).map(iu -> {
				FeatureDocBuilder builder = new FeatureDocBuilder();
				builder.setId(iu.getId());
				builder.setName(iu.getProperty(IInstallableUnit.PROP_NAME));
				builder.setVersion(iu.getVersion().getOriginal());
				builder.setDescription(iu.getProperty(IInstallableUnit.PROP_DESCRIPTION));
				builder.setDescriptionUrl(iu.getProperty(IInstallableUnit.PROP_DESCRIPTION_URL));
				builder.setProvider(iu.getProperty(IInstallableUnit.PROP_PROVIDER));
				builder.setContact(iu.getProperty(IInstallableUnit.PROP_CONTACT));
				builder.setDocumentationUrl(iu.getProperty(IInstallableUnit.PROP_DOC_URL));
				iu.getLicenses(null).stream().findFirst().ifPresent(license -> {
					builder.setLicense(license.getBody());
					if (license.getLocation() != null) {
						builder.setLicenseUrl(license.getLocation().toString());
					}
				});
				if (iu.getCopyright() != null) {
					builder.setCopyright(iu.getCopyright().getBody());
					if (iu.getCopyright().getLocation() != null) {
						builder.setCopyrightUrl(iu.getCopyright().getLocation().toString());
					}
				}
				return builder.build();

			}).collect(Collectors.toList());

		} catch (ProvisionException e) {
			throw new IllegalStateException(e);
		} finally {
			if (provisioningAgent != null) {
				provisioningAgent.stop();
			}
			Activator.getContext().ungetService(serviceReference);
		}
	}

	private P2InformationReader() {
		// try (just a little bit harder) -- JJ
	}

}
