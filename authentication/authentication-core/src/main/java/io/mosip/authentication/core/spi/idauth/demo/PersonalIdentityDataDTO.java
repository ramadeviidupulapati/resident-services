package io.mosip.authentication.core.spi.idauth.demo;

import io.mosip.authentication.core.dto.indauth.BioDTO;
import io.mosip.authentication.core.dto.indauth.BioType;
import io.mosip.authentication.core.dto.indauth.DemoDTO;
import io.mosip.authentication.core.dto.indauth.PinDTO;
import io.mosip.authentication.core.dto.indauth.PinType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Factory class.
 *
 * @author Rakesh Roshan
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PersonalIdentityDataDTO {

	/** Factory class for demographic details */
	private DemoDTO demoDTO;

	/** BioDTO for {@link BioType} */
	private BioDTO bioDTO;

	/** PinDTO for {@link PinType} */
	private PinDTO pinDTO;

}
