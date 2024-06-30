package com.mxtech;

import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.Locale;
import java.util.MissingResourceException;

public class LocaleUtils {
    public static final int FLAG_ISO639_2B = 2;
    public static final int FLAG_ISO639_2T = 1;
    public static final String UNDEFINED = "und";
    private static final String MAPPING_3T_to_2 = "aaraaabkabafrafakaakamhamaraararganasmasavaavaveaeaymayazeazbakbabambmbelbebenbnbihbhbisbibodbobosbsbrebrbulbgcatcacescschachchecechucuchvcvcorkwcoscocrecrcymcydandadeudedivdvdzodzellelengenepoeoesteteuseueweeefaofofasfafijfjfinfifrafrfryfyfulffglagdglegaglgglglvgvgrngngujguhaththauhahebiwherhzhinhihmohohrvhrhunhuhyehyiboigidoioiiiiiikuiuileieinaiaindinipkikislisitaitjavjvjpnjakalklkanknkaskskatkakaukrkazkkkhmkmkikkikinrwkirkykomkvkonkgkorkokuakjkurkulaololatlalavlvlimlilinlnlitltltzlblubluluglgmahmhmalmlmarmrmkdmkmlgmgmltmtmonmnmrimimsamsmyamynaunanavnvnblnrndendndongnepnenldnlnnonnnobnbnornonyanyociocojiojoriorormomossospanpaplipipolplporptpuspsquequrohrmronrorunrnrusrusagsgsansasinsislkskslvslsmesesmosmsnasnsndsdsomsosotstspaessqisqsrdscsrpsrsswsssunsuswaswswesvtahtytamtatatttteltetgktgtgltlthathtirtitontotsntntsotstuktkturtrtwitwuigugukrukurduruzbuzvenvevievivolvowlnwawolwoxhoxhyidyiyoryozhazazhozhzulzu";
    private static Converter _converter_3T_to_2 = new Converter(3, 2, MAPPING_3T_to_2);
    private static final String MAPPING_3B_to_2 = "aaraaabkabafrafakaakalbsqamhamaraararganarmhyasmasavaavaveaeaymayazeazbakbabambmbaqeubelbebenbnbihbhbisbibosbsbrebrbulbgburmycatcachachchecechizhchucuchvcvcorkwcoscocrecrczecsdandadivdvdutnldzodzengenepoeoesteteweeefaofofijfjfinfifrefrfryfyfulffgeokagerdeglagdglegaglgglglvgvgreelgrngngujguhaththauhahebiwherhzhinhihmohohrvhrhunhuiboigiceisidoioiiiiiikuiuileieinaiaindinipkikitaitjavjvjpnjakalklkanknkaskskaukrkazkkkhmkmkikkikinrwkirkykomkvkonkgkorkokuakjkurkulaololatlalavlvlimlilinlnlitltltzlblubluluglgmacmkmahmhmalmlmaomimarmrmaymsmlgmgmltmtmonmnnaunanavnvnblnrndendndongnepnennonnnobnbnornonyanyociocojiojoriorormomossospanpaperfaplipipolplporptpuspsquequrohrmrumrorunrnrusrusagsgsansasinsisloskslvslsmesesmosmsnasnsndsdsomsosotstspaessrdscsrpsrsswsssunsuswaswswesvtahtytamtatatttteltetgktgtgltlthathtibbotirtitontotsntntsotstuktkturtrtwitwuigugukrukurduruzbuzvenvevievivolvowelcywlnwawolwoxhoxhyidyiyoryozhazazulzu";
    private static Converter _converter_3B_to_2 = new Converter(3, 2, MAPPING_3B_to_2);
    private static final String MAPPING_2_to_3T = "aaaarababkaeaveafafrakakaamamhanargararaasasmavavaayaymazazebabakbebelbgbulbhbihbibisbmbambnbenbobodbrbrebsboscacatcechechchacocoscrcrecscescuchucvchvcycymdadandedeudvdivdzdzoeeeweelellenengeoepoesspaetesteueusfafasfffulfifinfjfijfofaofrfrafyfrygaglegdglaglglggngrngugujgvglvhahauhehebhihinhohmohrhrvhthathuhunhyhyehzheriainaidindieileigiboiiiiiikipkinindioidoisislititaiuikuiwhebjajpnjvjavkakatkgkonkikikkjkuakkkazklkalkmkhmknkankokorkrkaukskaskukurkvkomkwcorkykirlalatlbltzlgluglilimlnlinlolaoltlitlulublvlavmgmlgmhmahmimrimkmkdmlmalmnmonmrmarmsmsamtmltmymyananaunbnobndndenenepngndonlnldnnnnononornrnblnvnavnynyaocociojojiomormororiososspapanpipliplpolpspusptporququermrohrnrunroronrurusrwkinsasanscsrdsdsndsesmesgsagsisinskslkslslvsmsmosnsnasosomsqsqisrsrpsssswstsotsusunsvsweswswatatamteteltgtgkththatitirtktuktltgltntsntotontrturtstsotttattwtwitytahuguigukukrururduzuzbvevenvivievovolwawlnwowolxhxhoyiyidyoyorzazhazhzhozuzul";
    private static Converter _converter_2_to_3T = new Converter(2, 3, MAPPING_2_to_3T);
    private static final String MAPPING_2_to_3B = "aaaarababkaeaveafafrakakaamamhanargararaasasmavavaayaymazazebabakbebelbgbulbhbihbibisbmbambnbenbotibbrbrebsboscacatcechechchacocoscrcrecsczecuchucvchvcyweldadandegerdvdivdzdzoeeeweelgreenengeoepoesspaetesteubaqfaperfffulfifinfjfijfofaofrfrefyfrygaglegdglaglglggngrngugujgvglvhahauhehebhihinhohmohrhrvhthathuhunhyarmhzheriainaidindieileigiboiiiiiikipkinindioidoisiceititaiuikuiwhebjajpnjvjavkageokgkonkikikkjkuakkkazklkalkmkhmknkankokorkrkaukskaskukurkvkomkwcorkykirlalatlbltzlgluglilimlnlinlolaoltlitlulublvlavmgmlgmhmahmimaomkmacmlmalmnmonmrmarmsmaymtmltmyburnanaunbnobndndenenepngndonldutnnnnononornrnblnvnavnynyaocociojojiomormororiososspapanpipliplpolpspusptporququermrohrnrunrorumrurusrwkinsasanscsrdsdsndsesmesgsagsisinsksloslslvsmsmosnsnasosomsqalbsrsrpsssswstsotsusunsvsweswswatatamteteltgtgkththatitirtktuktltgltntsntotontrturtstsotttattwtwitytahuguigukukrururduzuzbvevenvivievovolwawlnwowolxhxhoyiyidyoyorzazhazhchizuzul";
    private static Converter _converter_2_to_3B = new Converter(2, 3, MAPPING_2_to_3B);


    public static class Converter {
        private final int _entrySize;
        private final int _inputSize;
        private final String _mapping;
        private final int _numMappings;

        Converter(int inputSize, int outputSize, String mapping) {
            this._inputSize = inputSize;
            this._entrySize = inputSize + outputSize;
            this._mapping = mapping;
            this._numMappings = mapping.length() / this._entrySize;
        }

        private int compareInputAt(String inputLang, int index) {
            int index2 = index * this._entrySize;
            int i = 0;
            while (i < this._inputSize) {
                int cmp = inputLang.charAt(i) - this._mapping.charAt(index2);
                if (cmp == 0) {
                    i++;
                    index2++;
                } else {
                    return cmp;
                }
            }
            return 0;
        }

        private String getMapped(int index) {
            int index2 = index * this._entrySize;
            return this._mapping.substring(this._inputSize + index2, this._entrySize + index2);
        }

        String getMapped(String inputLang) {
            String inputLang2 = inputLang.toLowerCase(Locale.US);
            int begin = 0;
            int end = this._numMappings;
            while (begin < end) {
                int index = begin + ((end - begin) / 2);
                int cmp = compareInputAt(inputLang2, index);
                if (cmp == 0) {
                    return getMapped(index);
                }
                if (cmp < 0) {
                    end = index;
                } else if (cmp > 0) {
                    begin = index + 1;
                }
            }
            return null;
        }
    }


    public static Locale[] parseLocales(@Nullable String value) {
        String[] split;
        if (value == null || value.length() == 0) {
            return new Locale[0];
        }
        ArrayList<Locale> locales = new ArrayList<>();
        for (String l : value.split("[\\s,\\.]+")) {
            Locale locale = parse(l);
            if (locale.getLanguage().length() > 0 && !locales.contains(locale)) {
                locales.add(locale);
            }
        }
        return (Locale[]) locales.toArray(new Locale[locales.size()]);
    }

    public static Locale parse(String text) {
        return parse(text, 3);
    }


    public static Locale parse(String text, int flags) {
        String variant;
        String country;
        String language;
        String[] parts = text.split("[_\\-]");
        if (parts.length > 0) {
            if (parts[0].length() == 3) {
                if ((flags & 1) != 0) {
                    language = _converter_3T_to_2.getMapped(parts[0]);
                } else {
                    language = null;
                }
                if (language == null && (flags & 2) != 0) {
                    language = _converter_3B_to_2.getMapped(parts[0]);
                }
                if (language == null) {
                    language = parts[0];
                }
            } else {
                language = parts[0];
            }
            if (parts.length > 1) {
                country = parts[1];
                if (parts.length > 2) {
                    variant = parts[2];
                } else {
                    variant = "";
                }
            } else {
                variant = "";
                country = "";
            }
        } else {
            variant = "";
            country = "";
            language = "";
        }
        Locale locale = new Locale(language, country, variant);
        return locale;
    }




    public static String getIOS3DefaultLanguage() {
        try {
            return Locale.getDefault().getISO3Language();
        } catch (MissingResourceException e) {
            return null;
        }
    }




}
