//
// Created by TimScriptov on 16.12.2020.
//

#ifndef MODDEDPE_COLOR_H
#define MODDEDPE_COLOR_H

class Color {
public:
    static const Color RED;
    float r, g, b, a;

    Color(float r, float g, float b) : r(r), g(g), b(b) {

    };
    Color(float a, float r, float g, float b) : a(a), r(r), g(g), b(b) {

    };
    static Color fromHSB(float, float, float);

    int toARGB() const;
    int toABGR() const;
};

#endif //MODDEDPE_COLOR_H
