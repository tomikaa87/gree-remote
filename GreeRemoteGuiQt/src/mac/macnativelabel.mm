#include "macnativelabel.h"

#import <AppKit/AppKit.h>

MacNativeLabel::MacNativeLabel(QWidget* parent)
    : QMacCocoaViewContainer(nullptr, parent)
{
    // Many Cocoa objects create temporary autorelease objects,
    // so create a pool to catch them.
    NSAutoreleasePool *pool = [[NSAutoreleasePool alloc] init];

    NSTextField *textField = [NSTextField labelWithString:@""];
    setCocoaView(textField);

    // Release our reference, since our super class takes ownership and we
    // don't need it anymore.
    [textField release];

    // Clean up our pool as we no longer need it.
    [pool release];
}

void MacNativeLabel::setText(const QString& text)
{
    auto textField = static_cast<NSTextField*>(cocoaView());
    textField.stringValue = [NSString stringWithUTF8String:text.toUtf8().constData()];
}
