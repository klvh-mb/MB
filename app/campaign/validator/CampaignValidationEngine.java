package campaign.validator;

import models.Campaign;
import models.CampaignActionsMeta;
import org.joda.time.DateTime;

/**
 * Created by IntelliJ IDEA.
 * Date: 8/12/14
 * Time: 11:08 PM
 * To change this template use File | Settings | File Templates.
 */
public class CampaignValidationEngine {
    private static final play.api.Logger logger = play.api.Logger.apply(CampaignValidationEngine.class);

    /**
     * @param campaign
     * @param userId
     * @return
     */
    public static ValidationResult validateCampaign(Campaign campaign, Long userId) {
        CampaignActionsMeta meta = CampaignActionsMeta.getMeta(campaign.getId());
        if (meta == null) {
            throw new IllegalArgumentException("Invalid campaignId: "+campaign.getId());
        }

        ICampaignValidator validator = getInstance(meta.getValidator());
        if (validator == null) {
            return ValidationResult.VALIDATOR_NOT_FOUND;
        }
        else {
            DateTime startTime = new DateTime(campaign.startDate.getTime());
            DateTime endTime = new DateTime(campaign.endDate.getTime());
            return validator.validate(userId, startTime, endTime);
        }
    }

    // get instance by java reflection
    private static ICampaignValidator getInstance(String fqClassName) {
        try {
            Class theClass = Class.forName(fqClassName);
            ICampaignValidator validator = (ICampaignValidator) theClass.newInstance();
            return validator;
        }
        catch (ClassNotFoundException e) {
            logger.underlyingLogger().error(fqClassName+" must be in class path.", e);
        }
        catch(InstantiationException e) {
            logger.underlyingLogger().error(fqClassName+" must be concrete.", e);
        }
        catch(IllegalAccessException e) {
            logger.underlyingLogger().error(fqClassName+" must have a no-arg constructor.", e);
        }
        return null;
    }
}
