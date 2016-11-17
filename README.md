# learntosing

这是教人唱歌的小样，从技术上实现分句打分功能，纯人声，非实时。项目分桌面后端desktopeditor数据编辑和android录音打分。

desktopeeditor:提供已有分句的自动旋律提取。

android:提供录制和打分功能，jni调用，将录音与标准旋律做拉伸对齐，实现打分。

项目使用wfft分析源码，频谱未做差值和平滑，效率和视觉上较粗糙。打分算法较土，属于人工智能，嗯，没毛病。

本项目可实现完备功能，maybe debug free，不做更新。

实时纠正，高精度频谱分析，自适应打分算法，多音效实现，产品级工程搭建正在v2项目完善中，请关注项目页。

This is a demo teach you singing, which technically realize scoreing, purely vocal, non-real time. The project contains desktop editor and android app.

Desktopeditor automatically pick up melody from a single seperated song sentence.

Android app can record and score, with jni implemented, which strenth the sample in alignment with the std melody and score.

The project use wfft opensource code, with no further optimization. The scoring algorithm is an experience implement, AI, ooops.

The project is functionally complete, maybe debug free, and will no more update.

In the future, real-time implement, high resolution fft, self-adaption ML algorithm, multi sound effect, production-level project is being improving, please keep eye on the project page.

sample apk: maybe later.

project url: http://miumini.com/@nightingale


