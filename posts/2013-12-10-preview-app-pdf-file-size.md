{ :slug "preview-app-pdf-file-size"
  :title "Preview.app PDF file size"
  :date "2013-12-10 20:24:25+00:00" 
  :tags #{:osx}}

------

You get a 11 Mb PDF from a (non-configurable) scanner and need to email/upload it to someone. You open it in Preview.app on osx, export, select the Reduce File Size quartz filter. Open the exported PDF.. and it looks like complete crap, not even remotely readable - thanks you so much Apple for the overzealous reduction filter. Here is a custom one you can add to /System/Library/Filters and get actually readable and decently small PDFs, save it as Shrink.qfilter or similar.

~~~ xml
<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE plist PUBLIC "-//Apple Computer//DTD PLIST 1.0//EN"
	"http://www.apple.com/DTDs/PropertyList-1.0.dtd">
<plist version="1.0">
<dict>
	<key>Domains</key>
	<dict>
		<key>Applications</key>
		<true/>
		<key>Printing</key>
		<true/>
	</dict>
	<key>FilterData</key>
	<dict>
		<key>ColorSettings</key>
		<dict>
			<key>DocumentColorSettings</key>
			<dict>
				<key>CustomLHSCorrection</key>
				<array>
					<integer>8</integer>
					<integer>8</integer>
					<integer>8</integer>
				</array>
			</dict>
			<key>ImageSettings</key>
			<dict>
				<key>Compression Quality</key>
				<real>0.5</real>
				<key>ImageCompression</key>
				<string>ImageJPEGCompress</string>
				<key>ImageScaleSettings</key>
				<dict>
					<key>ImageScaleFactor</key>
					<real>0.5</real>
					<key>ImageScaleInterpolate</key>
					<true/>
					<key>ImageSizeMax</key>
					<integer>1600</integer>
					<key>ImageSizeMin</key>
					<integer>128</integer>
				</dict>
			</dict>
		</dict>
	</dict>
	<key>FilterType</key>
	<integer>1</integer>
	<key>Name</key>
	<string>Shrink</string>
</dict>
</plist>
~~~
