#ifndef MACNATIVELABEL_H
#define MACNATIVELABEL_H

#include <QMacCocoaViewContainer>

class MacNativeLabel : public QMacCocoaViewContainer
{
public:
    explicit MacNativeLabel(QWidget* parent = nullptr);

    void setText(const QString& text);
};

#endif // MACNATIVELABEL_H
