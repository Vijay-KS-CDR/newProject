import 'package:flutter/material.dart';
import 'package:google_fonts/google_fonts.dart';
import 'screens/splash_screen.dart';
import 'screens/library_screen.dart';
import 'screens/player_screen.dart';

import 'services/stats_service.dart';

void main() async {
  WidgetsFlutterBinding.ensureInitialized();
  await StatsService().init();
  runApp(const ExtractZApp());
}

class ExtractZApp extends StatelessWidget {
  const ExtractZApp({super.key});

  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      title: 'ExtractZ',
      debugShowCheckedModeBanner: false,
      theme: ThemeData(
        useMaterial3: true,
        brightness: Brightness.dark,
        scaffoldBackgroundColor: Colors.black,
        primaryColor: const Color(0xFF8B5CF6), // Violet
        colorScheme: const ColorScheme.dark(
          primary: Color(0xFF8B5CF6),
          secondary: Color(0xFFA78BFA),
          surface: Color(0xFF1E1E1E),
          background: Colors.black,
        ),
        textTheme: GoogleFonts.outfitTextTheme(ThemeData.dark().textTheme),
      ),
      initialRoute: '/',
      routes: {
        '/': (context) => const SplashScreen(),
        '/library': (context) => const LibraryScreen(),
        '/player': (context) => const PlayerScreen(),
      },
    );
  }
}
