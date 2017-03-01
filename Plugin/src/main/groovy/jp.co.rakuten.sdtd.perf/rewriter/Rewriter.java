package jp.co.rakuten.sdtd.perf.rewriter;

import java.io.File;

import org.objectweb.asm.ClassReader;

import jp.co.rakuten.sdtd.perf.rewriter.classes.ClassFilter;
import jp.co.rakuten.sdtd.perf.rewriter.classes.ClassJar;
import jp.co.rakuten.sdtd.perf.rewriter.classes.ClassJarMaker;
import jp.co.rakuten.sdtd.perf.rewriter.classes.ClassProvider;
import jp.co.rakuten.sdtd.perf.rewriter.classes.ClassWriter;
import jp.co.rakuten.sdtd.perf.rewriter.detours.Detour;
import jp.co.rakuten.sdtd.perf.rewriter.detours.DetourLoader;
import jp.co.rakuten.sdtd.perf.rewriter.detours.Detourer;
import jp.co.rakuten.sdtd.perf.rewriter.mixins.Mixer;
import jp.co.rakuten.sdtd.perf.rewriter.mixins.MixinLoader;

public class Rewriter {
	
	public String input;
	public String outputJar;
	public String tempJar;
	public String classpath;
	public String exclude;
	public final Log log;
	    
	public static void main(String[] args) {
		Rewriter rewriter = new Rewriter();
		
		// testapp
		//rewriter.input = "C:\\petr\\project\\rems\\perf\\git\\performance-tracking\\android\\eclipse\\runtime\\target\\runtime-0.0.1-SNAPSHOT.jar;C:\\Users\\petr.luner\\AppData\\Local\\Android\\sdk\\extras\\android\\m2repository\\com\\android\\support\\support-annotations\\24.2.0\\support-annotations-24.2.0.jar;C:\\petr\\project\\rems\\perf\\git\\performance-tracking\\android\\testapp\\app\\build\\intermediates\\exploded-aar\\com.android.support\\appcompat-v7\\24.2.0\\jars\\classes.jar;C:\\petr\\project\\rems\\perf\\git\\performance-tracking\\android\\testapp\\app\\build\\intermediates\\exploded-aar\\com.android.support\\animated-vector-drawable\\24.2.0\\jars\\classes.jar;C:\\petr\\project\\rems\\perf\\git\\performance-tracking\\android\\testapp\\app\\build\\intermediates\\exploded-aar\\com.android.support\\support-vector-drawable\\24.2.0\\jars\\classes.jar;C:\\petr\\project\\rems\\perf\\git\\performance-tracking\\android\\testapp\\app\\build\\intermediates\\exploded-aar\\com.android.support\\support-v4\\24.2.0\\jars\\classes.jar;C:\\petr\\project\\rems\\perf\\git\\performance-tracking\\android\\testapp\\app\\build\\intermediates\\exploded-aar\\com.android.support\\support-fragment\\24.2.0\\jars\\classes.jar;C:\\petr\\project\\rems\\perf\\git\\performance-tracking\\android\\testapp\\app\\build\\intermediates\\exploded-aar\\com.android.support\\support-fragment\\24.2.0\\jars\\libs\\internal_impl-24.2.0.jar;C:\\petr\\project\\rems\\perf\\git\\performance-tracking\\android\\testapp\\app\\build\\intermediates\\exploded-aar\\com.android.support\\support-core-utils\\24.2.0\\jars\\classes.jar;C:\\petr\\project\\rems\\perf\\git\\performance-tracking\\android\\testapp\\app\\build\\intermediates\\exploded-aar\\com.android.support\\support-core-utils\\24.2.0\\jars\\libs\\internal_impl-24.2.0.jar;C:\\petr\\project\\rems\\perf\\git\\performance-tracking\\android\\testapp\\app\\build\\intermediates\\exploded-aar\\com.android.support\\support-media-compat\\24.2.0\\jars\\classes.jar;C:\\petr\\project\\rems\\perf\\git\\performance-tracking\\android\\testapp\\app\\build\\intermediates\\exploded-aar\\com.android.support\\support-media-compat\\24.2.0\\jars\\libs\\internal_impl-24.2.0.jar;C:\\petr\\project\\rems\\perf\\git\\performance-tracking\\android\\testapp\\app\\build\\intermediates\\exploded-aar\\com.android.support\\support-core-ui\\24.2.0\\jars\\classes.jar;C:\\petr\\project\\rems\\perf\\git\\performance-tracking\\android\\testapp\\app\\build\\intermediates\\exploded-aar\\com.android.support\\support-core-ui\\24.2.0\\jars\\libs\\internal_impl-24.2.0.jar;C:\\petr\\project\\rems\\perf\\git\\performance-tracking\\android\\testapp\\app\\build\\intermediates\\exploded-aar\\com.android.support\\support-compat\\24.2.0\\jars\\classes.jar;C:\\petr\\project\\rems\\perf\\git\\performance-tracking\\android\\testapp\\app\\build\\intermediates\\exploded-aar\\com.android.support\\support-compat\\24.2.0\\jars\\libs\\internal_impl-24.2.0.jar;C:\\petr\\project\\rems\\perf\\git\\performance-tracking\\android\\testapp\\app\\build\\intermediates\\classes\\debug";
		//rewriter.outputJar = "C:\\petr\\project\\rems\\perf\\git\\performance-tracking\\android\\testapp\\app\\build\\intermediates\\transforms\\PerfTracking\\debug\\jars\\1\\1f\\classes.jar";
		//rewriter.tempJar = "C:\\petr\\project\\rems\\perf\\git\\performance-tracking\\android\\testapp\\app\\build\\tmp\\transformClassesWithPerfTrackingForDebug\\classes.jar";
		//rewriter.classpath = "C:\\Users\\petr.luner\\AppData\\Local\\Android\\sdk\\platforms\\android-23\\android.jar";		
		//rewriter.exclude = "android";
		
		// rakubin
		rewriter.input = "C:\\petr\\project\\rems\\perf\\git\\performance-tracking\\android\\rakubin\\app\\libs\\adobeMobileLibrary-4.8.3.jar;C:\\petr\\project\\rems\\perf\\git\\performance-tracking\\android\\rakubin\\app\\libs\\mobileservices-1.1.5.jar;C:\\petr\\project\\rems\\perf\\git\\performance-tracking\\android\\eclipse\\runtime\\target\\runtime-0.0.1-SNAPSHOT.jar;C:\\petr\\project\\rems\\perf\\git\\performance-tracking\\android\\rakubin\\app\\libs\\notification-hubs-0.4.jar;C:\\petr\\project\\rems\\perf\\git\\performance-tracking\\android\\rakubin\\app\\libs\\notifications-1.0.1.jar;C:\\Users\\petr.luner\\.gradle\\caches\\modules-2\\files-2.1\\com.github.microsoft.telemetry-client-for-android\\SharedTelemetryContracts\\2.0.0\\c83e74a940f020a96a850d71a60ff3c576b9fc85\\SharedTelemetryContracts-2.0.0.jar;C:\\Users\\petr.luner\\.gradle\\caches\\modules-2\\files-2.1\\javax.inject\\javax.inject\\1\\6975da39a7040257bd51d21a231b76c915872d38\\javax.inject-1.jar;C:\\Users\\petr.luner\\.gradle\\caches\\modules-2\\files-2.1\\com.google\\volley\\1.0.19\\b5398443cc99ef91866ac08d5c2a702cde6ca0a5\\volley-1.0.19.jar;C:\\Users\\petr.luner\\.gradle\\caches\\modules-2\\files-2.1\\org.threeten\\threetenbp\\1.3.1\\5769e9c27cd5ba74cd3a73785dde0bbb5a2d3c0d\\threetenbp-1.3.1.jar;C:\\Users\\petr.luner\\.gradle\\caches\\modules-2\\files-2.1\\com.crashlytics.android\\crashlytics\\1.1.13\\e821eafa1bf489a26bdb71f95078c26785b37a1\\crashlytics-1.1.13.jar;C:\\Users\\petr.luner\\AppData\\Local\\Android\\sdk\\extras\\android\\m2repository\\com\\android\\support\\support-annotations\\23.1.1\\support-annotations-23.1.1.jar;C:\\Users\\petr.luner\\.gradle\\caches\\modules-2\\files-2.1\\jp.co.rakuten.sdtd\\mock-annotation\\3.0.1\\bc172881d8dbb2e79018b6103fb559e192703cc3\\mock-annotation-3.0.1.jar;C:\\Users\\petr.luner\\.gradle\\caches\\modules-2\\files-2.1\\com.nostra13.universalimageloader\\universal-image-loader\\1.9.4\\dd9eeb557650c4ad7394df23f0f5cf27a7cfc697\\universal-image-loader-1.9.4.jar;C:\\Users\\petr.luner\\.gradle\\caches\\modules-2\\files-2.1\\com.google.auto.service\\auto-service\\1.0-rc1\\f4f5ad098bf9a3c4fc95c15214c4ea014c38ac39\\auto-service-1.0-rc1.jar;C:\\Users\\petr.luner\\.gradle\\caches\\modules-2\\files-2.1\\me.tatarka\\auto-parcel-gson\\0.1\\74f6dacdf92ed34a39e25e6f935f6876dd1c0a1d\\auto-parcel-gson-0.1.jar;C:\\petr\\project\\rems\\perf\\git\\performance-tracking\\android\\rakubin\\app\\libs\\SaxRassReader-0.0.1.jar;C:\\Users\\petr.luner\\.gradle\\caches\\modules-2\\files-2.1\\com.github.frankiesardo\\auto-parcel\\0.3.1\\501393158454717fef27cba8d177f32a158b8955\\auto-parcel-0.3.1.jar;C:\\Users\\petr.luner\\.gradle\\caches\\modules-2\\files-2.1\\com.apptimize\\apptimize-android\\2.6.8\\887a4057c734239da9c1c9c2e3258412272d1717\\apptimize-android-2.6.8.jar;C:\\Users\\petr.luner\\.gradle\\caches\\modules-2\\files-2.1\\com.google.auto.factory\\auto-factory\\0.1-beta1\\76e414902f0a357eba8d43dc81fff2b80c67deb5\\auto-factory-0.1-beta1.jar;C:\\Users\\petr.luner\\.gradle\\caches\\modules-2\\files-2.1\\com.jakewharton\\butterknife\\5.1.1\\b3652dfd65ca0bcf18790ff1a1f7624af4cb2bb8\\butterknife-5.1.1.jar;C:\\Users\\petr.luner\\.gradle\\caches\\modules-2\\files-2.1\\com.google.code.findbugs\\jsr305\\2.0.3\\5871fb60dc68d67da54a663c3fd636a10a532948\\jsr305-2.0.3.jar;C:\\Users\\petr.luner\\.gradle\\caches\\modules-2\\files-2.1\\org.jsoup\\jsoup\\1.7.3\\92568d7167ce1bf9eb1fd815b022d5a2c113547a\\jsoup-1.7.3.jar;C:\\Users\\petr.luner\\.gradle\\caches\\modules-2\\files-2.1\\com.squareup.dagger\\dagger\\1.2.0\\adec009bc3b39d0cbd7ec96ef6014667a3a8f147\\dagger-1.2.0.jar;C:\\Users\\petr.luner\\.gradle\\caches\\modules-2\\files-2.1\\org.apache.commons\\commons-lang3\\3.0\\8873bd0bb5cb9ee37f1b04578eb7e26fcdd44cb0\\commons-lang3-3.0.jar;C:\\Users\\petr.luner\\.gradle\\caches\\modules-2\\files-2.1\\javax.annotation\\jsr250-api\\1.0\\5025422767732a1ab45d93abfea846513d742dcf\\jsr250-api-1.0.jar;C:\\Users\\petr.luner\\.gradle\\caches\\modules-2\\files-2.1\\org.aspectj\\aspectjrt\\1.8.4\\d4f6592d1114c0cd0c82796c164df7203117e708\\aspectjrt-1.8.4.jar;C:\\Users\\petr.luner\\.gradle\\caches\\modules-2\\files-2.1\\com.google.code.gson\\gson\\2.5\\686c608d1805b6d4d425ec4459e88164ffc85870\\gson-2.5.jar;C:\\petr\\project\\rems\\perf\\git\\performance-tracking\\android\\rakubin\\app\\build\\intermediates\\exploded-aar\\com.android.support\\multidex\\1.0.1\\jars\\classes.jar;C:\\petr\\project\\rems\\perf\\git\\performance-tracking\\android\\rakubin\\app\\build\\intermediates\\exploded-aar\\jp.co.rakuten.sdtd\\user\\4.0.2\\jars\\classes.jar;C:\\petr\\project\\rems\\perf\\git\\performance-tracking\\android\\rakubin\\app\\build\\intermediates\\exploded-aar\\jp.co.rakuten.api\\rae-memberinformation\\1.0.0\\jars\\classes.jar;C:\\petr\\project\\rems\\perf\\git\\performance-tracking\\android\\rakubin\\app\\build\\intermediates\\exploded-aar\\jp.co.rakuten.api\\rae-globalmemberinformation\\1.0.0\\jars\\classes.jar;C:\\petr\\project\\rems\\perf\\git\\performance-tracking\\android\\rakubin\\app\\build\\intermediates\\exploded-aar\\com.android.support\\design\\23.1.1\\jars\\classes.jar;C:\\petr\\project\\rems\\perf\\git\\performance-tracking\\android\\rakubin\\app\\build\\intermediates\\exploded-aar\\jp.co.rakuten.sdtd\\user-authenticator\\4.0.2\\jars\\classes.jar;C:\\petr\\project\\rems\\perf\\git\\performance-tracking\\android\\rakubin\\app\\build\\intermediates\\exploded-aar\\jp.co.rakuten.api\\rae-engine\\1.0.0\\jars\\classes.jar;C:\\petr\\project\\rems\\perf\\git\\performance-tracking\\android\\rakubin\\app\\build\\intermediates\\exploded-aar\\jp.co.rakuten.sdtd\\ping\\3.0.0\\jars\\classes.jar;C:\\petr\\project\\rems\\perf\\git\\performance-tracking\\android\\rakubin\\app\\build\\intermediates\\exploded-aar\\jp.co.rakuten.sdtd\\discover\\3.1.1\\jars\\classes.jar;C:\\petr\\project\\rems\\perf\\git\\performance-tracking\\android\\rakubin\\app\\build\\intermediates\\exploded-aar\\jp.co.rakuten.sdtd\\feedback\\2.0.2\\jars\\classes.jar;C:\\petr\\project\\rems\\perf\\git\\performance-tracking\\android\\rakubin\\app\\build\\intermediates\\exploded-aar\\jp.co.rakuten.sdtd\\deviceinformation\\2.0.0\\jars\\classes.jar;C:\\petr\\project\\rems\\perf\\git\\performance-tracking\\android\\rakubin\\app\\build\\intermediates\\exploded-aar\\jp.co.rakuten.sdtd\\mock\\3.0.1\\jars\\classes.jar;C:\\petr\\project\\rems\\perf\\git\\performance-tracking\\android\\rakubin\\app\\build\\intermediates\\exploded-aar\\api-1.0\\jars\\classes.jar;C:\\petr\\project\\rems\\perf\\git\\performance-tracking\\android\\rakubin\\app\\build\\intermediates\\exploded-aar\\jp.co.rakuten.sdtd\\versiontracker\\2.0.0\\jars\\classes.jar;C:\\petr\\project\\rems\\perf\\git\\performance-tracking\\android\\rakubin\\app\\build\\intermediates\\exploded-aar\\jp.co.rakuten.api\\core\\1.0.1\\jars\\classes.jar;C:\\petr\\project\\rems\\perf\\git\\performance-tracking\\android\\rakubin\\app\\build\\intermediates\\exploded-aar\\com.google.android.gms\\play-services-ads\\7.3.0\\jars\\classes.jar;C:\\petr\\project\\rems\\perf\\git\\performance-tracking\\android\\rakubin\\app\\build\\intermediates\\exploded-aar\\com.google.android.gms\\play-services-analytics\\7.3.0\\jars\\classes.jar;C:\\petr\\project\\rems\\perf\\git\\performance-tracking\\android\\rakubin\\app\\build\\intermediates\\exploded-aar\\com.google.android.gms\\play-services-gcm\\7.3.0\\jars\\classes.jar;C:\\petr\\project\\rems\\perf\\git\\performance-tracking\\android\\rakubin\\app\\build\\intermediates\\exploded-aar\\com.google.android.gms\\play-services-location\\7.3.0\\jars\\classes.jar;C:\\petr\\project\\rems\\perf\\git\\performance-tracking\\android\\rakubin\\app\\build\\intermediates\\exploded-aar\\com.google.android.gms\\play-services-maps\\7.3.0\\jars\\classes.jar;C:\\petr\\project\\rems\\perf\\git\\performance-tracking\\android\\rakubin\\app\\build\\intermediates\\exploded-aar\\com.google.android.gms\\play-services-wearable\\7.3.0\\jars\\classes.jar;C:\\petr\\project\\rems\\perf\\git\\performance-tracking\\android\\rakubin\\app\\build\\intermediates\\exploded-aar\\com.google.android.gms\\play-services-base\\7.3.0\\jars\\classes.jar;C:\\petr\\project\\rems\\perf\\git\\performance-tracking\\android\\rakubin\\app\\build\\intermediates\\exploded-aar\\com.mixpanel.android\\mixpanel-android\\4.6.4\\jars\\classes.jar;C:\\petr\\project\\rems\\perf\\git\\performance-tracking\\android\\rakubin\\app\\build\\intermediates\\exploded-aar\\com.android.support\\cardview-v7\\21.0.3\\jars\\classes.jar;C:\\petr\\project\\rems\\perf\\git\\performance-tracking\\android\\rakubin\\app\\build\\intermediates\\exploded-aar\\com.android.support\\recyclerview-v7\\23.1.1\\jars\\classes.jar;C:\\petr\\project\\rems\\perf\\git\\performance-tracking\\android\\rakubin\\app\\build\\intermediates\\exploded-aar\\com.android.support\\appcompat-v7\\23.1.1\\jars\\classes.jar;C:\\petr\\project\\rems\\perf\\git\\performance-tracking\\android\\rakubin\\app\\build\\intermediates\\exploded-aar\\com.android.support\\support-v4\\23.1.1\\jars\\classes.jar;C:\\petr\\project\\rems\\perf\\git\\performance-tracking\\android\\rakubin\\app\\build\\intermediates\\exploded-aar\\com.android.support\\support-v4\\23.1.1\\jars\\libs\\internal_impl-23.1.1.jar;C:\\petr\\project\\rems\\perf\\git\\performance-tracking\\android\\rakubin\\app\\build\\intermediates\\exploded-aar\\co.jp.cyberz.fox\\sdk-android\\v2.15.7g\\jars\\classes.jar;C:\\petr\\project\\rems\\perf\\git\\performance-tracking\\android\\rakubin\\app\\build\\intermediates\\exploded-aar\\library-2.4.1\\jars\\classes.jar;C:\\petr\\project\\rems\\perf\\git\\performance-tracking\\android\\rakubin\\app\\build\\intermediates\\exploded-aar\\com.microsoft.azure\\applicationinsights-android\\1.0-beta.10\\jars\\classes.jar;C:\\petr\\project\\rems\\perf\\git\\performance-tracking\\android\\rakubin\\app\\build\\intermediates\\exploded-aar\\com.github.microsoft.telemetry-client-for-android\\AndroidCll\\2.0.0\\jars\\classes.jar;C:\\petr\\project\\rems\\perf\\git\\performance-tracking\\android\\rakubin\\app\\build\\intermediates\\exploded-aar\\in.srain.cube\\ultra-ptr\\1.0.7\\jars\\classes.jar;C:\\petr\\project\\rems\\perf\\git\\performance-tracking\\android\\rakubin\\app\\build\\intermediates\\exploded-aar\\com.github.hotchemi\\android-rate\\0.4.3\\jars\\classes.jar;C:\\petr\\project\\rems\\perf\\git\\performance-tracking\\android\\rakubin\\app\\build\\intermediates\\classes\\debug";
		rewriter.outputJar = "C:\\petr\\project\\rems\\perf\\git\\performance-tracking\\android\\rakubin\\app\\build\\intermediates\\transforms\\PerfTracking\\debug\\jars\\1\\1f\\classes.jar";
		rewriter.tempJar = "C:\\petr\\project\\rems\\perf\\git\\performance-tracking\\android\\rakubin\\app\\build\\tmp\\transformClassesWithPerfTrackingForDebug\\classes.jar";
		rewriter.classpath = "C:\\Users\\petr.luner\\AppData\\Local\\Android\\sdk\\platforms\\android-23\\android.jar";
		rewriter.exclude = "com.apptimize;com.crashlytics";

		//rewriter.log.level = Log.NONE;
		rewriter.log.level = Log.INFO;
		//rewriter.log.level = Log.DEBUG;
		rewriter.rewrite();
	}
	
	public Rewriter() {
		log = new Log();
	}
	
	public void rewrite() {
		
		System.out.println(input);
		log.debug("Populating temp JAR");
		ClassJarMaker tempMaker = new ClassJarMaker(new File(tempJar));
		try {
			tempMaker.populate(input);
		}
		finally {
			tempMaker.Close();
		}
		
		ClassJar temp = new ClassJar(new File(tempJar));
		ClassProvider provider = new ClassProvider(classpath + File.pathSeparator + tempJar);
		
		DetourLoader detourLoader = new DetourLoader(log);
		Detourer detourer = new Detourer();
		
		MixinLoader mixinLoader = new MixinLoader(log);
		Mixer mixer = new Mixer();
					
		for (String name : temp.getClasses()) {
			if (name.startsWith("jp.co.rakuten.sdtd.perf.runtime.detours.")) {
				log.debug("Found detours " + name);
				for (Detour detour : detourLoader.load(temp.getClassNode(name))) {
					detourer.add(detour);
				}
			}
			else if (name.startsWith("jp.co.rakuten.sdtd.perf.runtime.mixins.")) {
				log.debug("Found mixin " + name);
				mixer.add(mixinLoader.loadMixin(temp.getClassNode(name)));
			}
		}

		ClassJarMaker outputMaker = new ClassJarMaker(new File(outputJar));
		try {
			ClassFilter filter = new ClassFilter();
			filter.exclude("jp.co.rakuten.sdtd.perf.runtime");
			filter.exclude(exclude);
			
			for (String name : temp.getClasses()) {
	
				if (filter.canRewrite(name)) { 
					log.debug("Rewriting class " + name);
					
					try {
						Class<?> clazz = provider.getClass(name);
						ClassReader cr = temp.getClassReader(name);
						ClassWriter cw = new ClassWriter(provider, ClassWriter.COMPUTE_MAXS | ClassWriter.COMPUTE_FRAMES);
						cr.accept(detourer.getAdapter(clazz, provider, mixer.getAdapter(clazz, cw)), 0);
						outputMaker.add(name, cw.toByteArray());
						
					} catch (Throwable e) {
						log.error("Failed to rewrite " + name, e);
						outputMaker.add(name, temp);
					}
				}
				else {
					outputMaker.add(name, temp);
				}
			}
		}
		finally {
			outputMaker.Close();
		}
	}
}
