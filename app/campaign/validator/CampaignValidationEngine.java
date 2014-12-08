package campaign.validator;

import models.CampaignActionsMeta;

/**
 * Created by IntelliJ IDEA.
 * Date: 8/12/14
 * Time: 11:08 PM
 * To change this template use File | Settings | File Templates.
 */
public class CampaignValidationEngine {
    private static final play.api.Logger logger = play.api.Logger.apply(CampaignValidationEngine.class);

    /**
     * @param campaignId
     * @return
     */
    public static ValidationResult validateCampaign(Long campaignId) {
        CampaignActionsMeta meta = CampaignActionsMeta.getMeta(campaignId);

        ICampaignValidator validator = getInstance(meta.getValidator());
        return validator.validate();
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
