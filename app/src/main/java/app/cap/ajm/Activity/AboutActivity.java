package app.cap.ajm.Activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import me.drakeet.multitype.Items;
import me.drakeet.support.about.AbsAboutActivity;
import me.drakeet.support.about.BuildConfig;
import me.drakeet.support.about.Card;
import me.drakeet.support.about.Category;
import me.drakeet.support.about.Contributor;
import me.drakeet.support.about.License;
import me.drakeet.support.about.Line;
import me.drakeet.support.about.R;

public class AboutActivity extends AbsAboutActivity {

    @Override @SuppressLint("SetTextI18n")
    protected void onCreateHeader(ImageView icon, TextView slogan, TextView version) {
        icon.setImageResource(R.drawable.about_pagemainlogo2);
        slogan.setText(getString(R.string.about_pageApp));
        version.setText("v" + BuildConfig.VERSION_NAME);
    }


    @Override @SuppressWarnings("SpellCheckingInspection")
    protected void onItemsCreated(@NonNull Items items) {
        /* @formatter:off */
        items.add(new Category(getString(R.string.about_pageHi)));
        items.add(new Card(getString(R.string.about_page).replace(" ", "\u00A0"), getString(R.string.about_pageSHARE)));
        items.add(new Line());
        items.add(new Category(getString(R.string.about_pageHelp)));
        items.add(new Contributor(R.drawable.about_pageic_my_location_black_48dp,getString(R.string.about_pageHow_gps), getString(R.string.about_pageGPS).replace(" ","\u00A0")));
        items.add(new Contributor(R.drawable.about_pageic_directions_bike_black_48dp ,getString(R.string.about_pageHow_Direct),getString(R.string.about_pageDirect).replace(" ","\u00A0")));
        items.add(new Contributor(R.drawable.about_pageic_trending_up_black_48dp,getString(R.string.about_pageHow_Route), getString(R.string.about_pageRoute).replace(" ","\u00A0")));
        items.add(new Contributor(R.drawable.about_pageic_sms_black_48dp,getString(R.string.about_pageHow_Save), getString(R.string.about_pageSave).replace(" ","\u00A0")));
        items.add(new Contributor(R.drawable.about_pageic_keyboard_voice_black_48dp,getString(R.string.about_pageHow_TTS),
                getString(R.string.about_pageTTS).replace(" ","\u00A0")));
        items.add(new Contributor(R.drawable.about_pageic_warning_black_48dp,getString(R.string.about_pageHow_Alert), getString(R.string.about_pageAlerts).replace(" ","\u00A0")));
        items.add(new Category(getString(R.string.about_pageAlert)));
        items.add(new Contributor(R.drawable.about_pagemainlogo2, getString(R.string.about_pageAJM), getString(R.string.about_pageAJM_Alert).replace(" ","\u00A0")));
        items.add(new Line());
        items.add(new Line());
        items.add(new Category(getString(R.string.about_pagePL)));
        items.add(new Contributor(R.drawable.about_pageic_android_black_48dp,getString(R.string.about_pageWhere), getString(R.string.about_pageWhere_is)));
        items.add(new Line());
        items.add(new Category(getString(R.string.about_pageCS)));
        items.add(new Contributor(R.drawable.about_pageic_star_black_48dp,getString(R.string.about_pageBike), getString(R.string.about_pageBikeis)));
        items.add(new Line());
        items.add(new Category(getString(R.string.about_pageLicense)));
        items.add(new License("MultiType", "drakeet",License.APACHE_2,"https://github.com/drakeet/MultiType"));
        items.add(new License("about-page", "drakeet", License.APACHE_2, "https://github.com/drakeet/about-page"));
        items.add(new License("EasyWeather","code-crusher",License.APACHE_2, "https://github.com/code-crusher/EasyWeather"));
        items.add(new License("SpeedMeter","flyingrub",License.GPL_V2,"https://github.com/flyingrub/SpeedMeter"));
        items.add(new License("Fall_Detection","swift2891",License.APACHE_2,"https://github.com/swift2891/Fall_Detection"));
        items.add(new License("picasso","square",License.APACHE_2, "https://github.com/square/picasso"));
        items.add(new License("okhttp","square", License.APACHE_2,"https://github.com/square/okhttp"));
        items.add(new License("volley","google", License.APACHE_2, "https://github.com/google/volley"));
        items.add(new License("butternife","JakeWharton",License.APACHE_2,"https://github.com/JakeWharton/butterknife"));
        items.add(new License("parcler","johncarl81",License.APACHE_2,"https://github.com/johncarl81/parceler"));
        items.add(new License("FloatingActionButton","makovkastar",License.MIT,"https://github.com/makovkastar/FloatingActionButton"));
        items.add(new License("AndroidSwipeLayout","daimajia",License.MIT,"https://github.com/daimajia/AndroidSwipeLayout"));
        items.add(new License("geofire-java","firebase",License.MIT,"https://github.com/firebase/geofire-java"));
        items.add(new License("CicleImageView","hdodenhof",License.APACHE_2,"https://github.com/hdodenhof/CircleImageView"));
        items.add(new License("commons-lang3","Apache",License.APACHE_2,"https://mvnrepository.com/artifact/org.apache.commons/commons-lang3/3.3.2"));
        items.add(new License("commons-math3","Apache",License.APACHE_2,"https://mvnrepository.com/artifact/org.apache.commons:commons-math3:3.6.1"));
        items.add(new License("gson","google",License.APACHE_2,"https://github.com/google/gson"));
        items.add(new License("Google Play Services API",License.GOOGLE, License.APACHE_2, "https://developers.google.com/android/reference"));
        items.add(new License("material-design-icons","google",License.APACHE_2,"https://github.com/google/material-design-icons"));
        items.add(new License("Kakao Open SDK",License.KAKAO,License.APACHE_2,"https://developers.kakao.com/docs/sdk"));
        items.add(new Line());
        items.add(new Category("Apache License\n" +
                "\n" +
                "Version 2.0, January 2004\n" +
                "\n" +
                "http://www.apache.org/licenses/ \n" +
                "\n" +
                "TERMS AND CONDITIONS FOR USE, REPRODUCTION, AND DISTRIBUTION\n" +
                "\n" +
                "1. Definitions.\n" +
                "\"License\" shall mean the terms and conditions for use, reproduction, and distribution as defined by Sections 1 through 9 of this document.\n" +
                "\"Licensor\" shall mean the copyright owner or entity authorized by the copyright owner that is granting the License.\n" +
                "\"Legal Entity\" shall mean the union of the acting entity and all other entities that control, are controlled by, or are under common control with that entity. For the purposes of this definition, \"control\" means (i) the power, direct or indirect, to cause the direction or management of such entity, whether by contract or otherwise, or (ii) ownership of fifty percent (50%) or more of the outstanding shares, or (iii) beneficial ownership of such entity.\n" +
                "\"You\" (or \"Your\") shall mean an individual or Legal Entity exercising permissions granted by this License.\n" +
                "\"Source\" form shall mean the preferred form for making modifications, including but not limited to software source code, documentation source, and configuration files.\n" +
                "\"Object\" form shall mean any form resulting from mechanical transformation or translation of a Source form, including but not limited to compiled object code, generated documentation, and conversions to other media types.\n" +
                "\"Work\" shall mean the work of authorship, whether in Source or Object form, made available under the License, as indicated by a copyright notice that is included in or attached to the work (an example is provided in the Appendix below).\n" +
                "\"Derivative Works\" shall mean any work, whether in Source or Object form, that is based on (or derived from) the Work and for which the editorial revisions, annotations, elaborations, or other modifications represent, as a whole, an original work of authorship. For the purposes of this License, Derivative Works shall not include works that remain separable from, or merely link (or bind by name) to the interfaces of, the Work and Derivative Works thereof.\n" +
                "\"Contribution\" shall mean any work of authorship, including the original version of the Work and any modifications or additions to that Work or Derivative Works thereof, that is intentionally submitted to Licensor for inclusion in the Work by the copyright owner or by an individual or Legal Entity authorized to submit on behalf of the copyright owner. For the purposes of this definition, \"submitted\" means any form of electronic, verbal, or written communication sent to the Licensor or its representatives, including but not limited to communication on electronic mailing lists, source code control systems, and issue tracking systems that are managed by, or on behalf of, the Licensor for the purpose of discussing and improving the Work, but excluding communication that is conspicuously marked or otherwise designated in writing by the copyright owner as \"Not a Contribution.\"\n" +
                "\"Contributor\" shall mean Licensor and any individual or Legal Entity on behalf of whom a Contribution has been received by Licensor and subsequently incorporated within the Work.\n" +
                "2. Grant of Copyright License. Subject to the terms and conditions of this License, each Contributor hereby grants to You a perpetual, worldwide, non-exclusive, no-charge, royalty-free, irrevocable copyright license to reproduce, prepare Derivative Works of, publicly display, publicly perform, sublicense, and distribute the Work and such Derivative Works in Source or Object form.\n" +
                "3. Grant of Patent License. Subject to the terms and conditions of this License, each Contributor hereby grants to You a perpetual, worldwide, non-exclusive, no-charge, royalty-free, irrevocable (except as stated in this section) patent license to make, have made, use, offer to sell, sell, import, and otherwise transfer the Work, where such license applies only to those patent claims licensable by such Contributor that are necessarily infringed by their Contribution(s) alone or by combination of their Contribution(s) with the Work to which such Contribution(s) was submitted. If You institute patent litigation against any entity (including a cross-claim or counterclaim in a lawsuit) alleging that the Work or a Contribution incorporated within the Work constitutes direct or contributory patent infringement, then any patent licenses granted to You under this License for that Work shall terminate as of the date such litigation is filed.\n" +
                "4. Redistribution. You may reproduce and distribute copies of the Work or Derivative Works thereof in any medium, with or without modifications, and in Source or Object form, provided that You meet the following conditions:\n" +
                "a.You must give any other recipients of the Work or Derivative Works a copy of this License; and\n" +
                "b.You must cause any modified files to carry prominent notices stating that You changed the files; and\n" +
                "c.You must retain, in the Source form of any Derivative Works that You distribute, all copyright, patent, trademark, and attribution notices from the Source form of the Work, excluding those notices that do not pertain to any part of the Derivative Works; and\n" +
                "d.If the Work includes a \"NOTICE\" text file as part of its distribution, then any Derivative Works that You distribute must include a readable copy of the attribution notices contained within such NOTICE file, excluding those notices that do not pertain to any part of the Derivative Works, in at least one of the following places: within a NOTICE text file distributed as part of the Derivative Works; within the Source form or documentation, if provided along with the Derivative Works; or, within a display generated by the Derivative Works, if and wherever such third-party notices normally appear. The contents of the NOTICE file are for informational purposes only and do not modify the License. You may add Your own attribution notices within Derivative Works that You distribute, alongside or as an addendum to the NOTICE text from the Work, provided that such additional attribution notices cannot be construed as modifying the License. \n" +
                "\n" +
                " You may add Your own copyright statement to Your modifications and may provide additional or different license terms and conditions for use, reproduction, or distribution of Your modifications, or for any such Derivative Works as a whole, provided Your use, reproduction, and distribution of the Work otherwise complies with the conditions stated in this License. \n" +
                "\n" +
                " 5. Submission of Contributions. Unless You explicitly state otherwise, any Contribution intentionally submitted for inclusion in the Work by You to the Licensor shall be under the terms and conditions of this License, without any additional terms or conditions. Notwithstanding the above, nothing herein shall supersede or modify the terms of any separate license agreement you may have executed with Licensor regarding such Contributions.\n" +
                " 6. Trademarks. This License does not grant permission to use the trade names, trademarks, service marks, or product names of the Licensor, except as required for reasonable and customary use in describing the origin of the Work and reproducing the content of the NOTICE file.\n" +
                " 7. Disclaimer of Warranty. Unless required by applicable law or agreed to in writing, Licensor provides the Work (and each Contributor provides its Contributions) on an \"AS IS\" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied, including, without limitation, any warranties or conditions of TITLE, NON-INFRINGEMENT, MERCHANTABILITY, or FITNESS FOR A PARTICULAR PURPOSE. You are solely responsible for determining the appropriateness of using or redistributing the Work and assume any risks associated with Your exercise of permissions under this License.\n" +
                " 8. Limitation of Liability. In no event and under no legal theory, whether in tort (including negligence), contract, or otherwise, unless required by applicable law (such as deliberate and grossly negligent acts) or agreed to in writing, shall any Contributor be liable to You for damages, including any direct, indirect, special, incidental, or consequential damages of any character arising as a result of this License or out of the use or inability to use the Work (including but not limited to damages for loss of goodwill, work stoppage, computer failure or malfunction, or any and all other commercial damages or losses), even if such Contributor has been advised of the possibility of such damages.\n" +
                " 9. Accepting Warranty or Additional Liability. While redistributing the Work or Derivative Works thereof, You may choose to offer, and charge a fee for, acceptance of support, warranty, indemnity, or other liability obligations and/or rights consistent with this License. However, in accepting such obligations, You may act only on Your own behalf and on Your sole responsibility, not on behalf of any other Contributor, and only if You agree to indemnify, defend, and hold each Contributor harmless for any liability incurred by, or claims asserted against, such Contributor by reason of your accepting any such warranty or additional liability.\n" +
                "\n" +
                "END OF TERMS AND CONDITIONS"));

        items.add(new Category("MIT License\n" +
                "\n" +
                "Copyright (c) <year> <copyright holders>\n" +
                "\n" +
                "Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the \"Software\"), \n" +
                "to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, \n" +
                "and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:\n" +
                "\n" +
                "The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.\n" +
                "\n" +
                "THE SOFTWARE IS PROVIDED \"AS IS\", \n" +
                "WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, \n" +
                "INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, \n" +
                "FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. \n" +
                "IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, \n" +
                "DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, \n" +
                "ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE \n" +
                "OR THE USE OR OTHER DEALINGS IN THE SOFTWARE."));
        items.add(new Category("GNU General Public License version 2\n" +
                " \n" +
                "SPDX short identifier: GPL-2.0\n" +
                "\n" +
                "GNU GENERAL PUBLIC LICENSE\n" +
                " Version 2, June 1991\n" +
                "\n" +
                "Copyright (C) 1989, 1991 Free Software Foundation, Inc.\n" +
                " 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA\n" +
                "\n" +
                "Everyone is permitted to copy and distribute verbatim copies\n" +
                " of this license document, but changing it is not allowed.\n" +
                " "));
        items.add(new Category("GNU GENERAL PUBLIC LICENSE\n" +
                "\n" +
                "Version 3, 29 June 2007\n" +
                "\n" +
                "Copyright © 2007 Free Software Foundation, Inc. <http://fsf.org/>\n" +
                "\n" +
                "Everyone is permitted to copy and distribute verbatim copies of this license document, but changing it is not allowed."));
    }
    @Override
    protected void onActionClick(View action) {
        onClickShare();
    }


    public void onClickShare() {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_SUBJECT, "안전모");
        intent.putExtra(Intent.EXTRA_TEXT, getString(R.string.about_page));
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(Intent.createChooser(intent, getTitle()));
    }
}


