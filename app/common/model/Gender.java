package common.model;

/**
 * Created by IntelliJ IDEA.
 * Date: 29/5/14
 * Time: 11:46 PM
 * To change this template use File | Settings | File Templates.
 */
public enum Gender {
    Male,
    Female;

    public static Gender valueOfStr(String genderStr) {
        if (genderStr == null) {
            throw new IllegalArgumentException("Input is null");
        }

        Gender result = Gender.Male;
        if ("FEMALE".equals(genderStr.trim().toUpperCase())) {
            result = Gender.Female;
        } else if (genderStr.trim().toUpperCase().contains("F")) {
            result = Gender.Female;
        }

        return result;
    }

    public static Gender valueOfInt(int genderInt) {
        switch(genderInt) {
            case 1:
                return Gender.Male;
            default:
                return Gender.Female;
        }
    }
}
