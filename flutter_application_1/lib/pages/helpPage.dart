import 'dart:io';

import 'package:flutter/material.dart';
import 'package:flutter/src/widgets/framework.dart';
import 'package:flutter/src/widgets/placeholder.dart';
import 'package:flutter_application_1/themes/ThemeModel.dart';
import 'package:hive/hive.dart';
import 'package:smooth_page_indicator/smooth_page_indicator.dart';
import '../themes/ThemeController.dart';

class helpPage extends StatefulWidget {
  const helpPage({super.key});

  @override
  State<helpPage> createState() => _helpPageState();
}

class _helpPageState extends State<helpPage> {
  ThemeController _themeController = ThemeController();
  final _themeBox = Hive.box('theme');
  // late PageController _pageController;
  PageController _pageController =
      PageController(viewportFraction: 1, initialPage: 1);

  final List<String> images = [
    'https://i.imgur.com/YY8YJWU.png',
    'https://i.imgur.com/N9LfCaY.png',
    'https://i.imgur.com/zmuasHU.png',
    'https://i.imgur.com/pblEM3z.png',
    'https://i.imgur.com/GzAWJnv.png',
    'https://i.imgur.com/2LybhDa.png',
    'https://i.imgur.com/hbNDVYf.png',
    'https://i.imgur.com/Qg38APw.png',
    'https://i.imgur.com/cT81OkL.png',
    'https://i.imgur.com/Msb1kjD.png'
  ];

  @override
  void initState() {
    super.initState();
    _pageController = PageController(viewportFraction: 1);
  }

  @override
  Widget build(BuildContext context) {
    ThemeModel theme = _themeController.getTheme(_themeBox.get('token'));
    int activePage = 0;
    // CloseButton()
    return Scaffold(
      backgroundColor: theme.background,
      body: SafeArea(
        child: Stack(
          children: [
            Align(
              alignment: Alignment.center,
              child: SizedBox(
                height: MediaQuery.of(context).size.height,
                width: MediaQuery.of(context).size.width,
                child: PageView.builder(
                    itemCount: images.length,
                    pageSnapping: true,
                    controller: _pageController,
                    onPageChanged: (page) {
                      setState(() {
                        activePage = page;
                      });
                    },
                    itemBuilder: (context, pagePosition) {
                      return Container(
                        child: Image.network(
                          images[pagePosition],
                          fit: BoxFit.contain,
                        ),
                      );
                    }),
              ),
            ),
            Align(
                alignment: Alignment.topRight,
                child: IconButton(
                    icon: Icon(Icons.close),
                    color: Color.fromARGB(255, 243, 120, 120),
                    onPressed: () => Navigator.of(context).pop())),
            Positioned(
              bottom: MediaQuery.of(context).size.height * 0.057,
              width: MediaQuery.of(context).size.width,
              child: Align(
                alignment: Alignment.center,
                child: SmoothPageIndicator(
                  controller: _pageController,
                  count: images.length,
                  effect: WormEffect(
                      activeDotColor: Color.fromARGB(255, 243, 120, 120),
                      dotHeight: MediaQuery.of(context).size.height * 0.018,
                      dotWidth: MediaQuery.of(context).size.height * 0.018),
                ),
              ),
            )
          ],
        ),
      ),
    );
  }
}
