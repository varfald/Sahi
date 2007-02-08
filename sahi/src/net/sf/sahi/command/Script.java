package net.sf.sahi.command;

import net.sf.sahi.playback.SahiScript;
import net.sf.sahi.request.HttpRequest;
import net.sf.sahi.response.HttpFileResponse;
import net.sf.sahi.response.HttpResponse;
import net.sf.sahi.util.Utils;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Copyright  2006  V Narayan Raman
 * <p/>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p/>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p/>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

public class Script {
    public HttpResponse view(HttpRequest request) {
        String file = request.getParameter("script");
        return view(file);
    }

    public HttpResponse view(String file) {
        String js = makeIncludeALink(file);

        Properties props = new Properties();
        props.setProperty("name", file);
        props.setProperty("js", js);
        props.setProperty("script", js);
        return new HttpFileResponse(net.sf.sahi.config.Configuration.getHtdocsRoot() + "spr/script.htm", props, false, true);
    }

    public static String makeIncludeALink(String baseFile) {
        String inputStr = new String(Utils.readFile(baseFile));
        String patternStr = "_include\\([\"'](.*)[\"']\\).*[\n]";
        Pattern pattern = Pattern.compile(patternStr);
        Matcher matcher = pattern.matcher(inputStr);

        StringBuffer sb = new StringBuffer();
        sb.append("\n//--START--" + baseFile.replaceAll("\\\\", "/") + "-----------------\n\n");
        while (matcher.find()) {
            String includeStatement = matcher.group(0);
            String includedScriptName = matcher.group(1);
//            String scriptPath = Utils.concatPaths(baseFile, includedScriptName).replaceAll("\\\\", "/");
//            String replaceStr = "<a href='/_s_/dyn/Script_view?script="+scriptPath+"'>"+includeStatement+"</a>";

            String scriptPath = Utils.concatPaths(baseFile, includedScriptName);
            String replaceStr = makeIncludeALink(scriptPath);

            matcher.appendReplacement(sb, replaceStr);
        }
        matcher.appendTail(sb);
        sb.append("\n//---END---" + baseFile.replaceAll("\\\\", "/") + "-----------------\n\n");
        return sb.toString();
    }

    HttpResponse dummyFunctions(HttpRequest request) {
        ArrayList words = SahiScript.getKeyWords();
        StringBuffer sb = new StringBuffer();
        for (Iterator iterator = words.iterator(); iterator.hasNext();) {
            String word = (String) iterator.next();
            sb.append("var " + word + " = b;\n");
            sb.append("var _" + word + " = b;\n");
        }
        String functions = sb.toString();

        Properties props = new Properties();
        props.setProperty("dummyFunctions", functions);
        return new HttpFileResponse(net.sf.sahi.config.Configuration.getHtdocsRoot() + "spr/dummyFunctions.js", props, false, true);
    }

}