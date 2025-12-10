import 'package:shared_preferences/shared_preferences.dart';

class StatsService {
  static final StatsService _instance = StatsService._internal();
  factory StatsService() => _instance;
  StatsService._internal();

  late SharedPreferences _prefs;
  bool _initialized = false;

  Future<void> init() async {
    if (_initialized) return;
    _prefs = await SharedPreferences.getInstance();
    _initialized = true;
  }

  // Keys
  String _getPlayCountKey(String id) => 'play_count_$id';
  String _getFavKey(String id) => 'fav_$id';

  // Play Counts
  int getPlayCount(String id) {
    return _prefs.getInt(_getPlayCountKey(id)) ?? 0;
  }

  Future<void> incrementPlayCount(String id) async {
    final current = getPlayCount(id);
    await _prefs.setInt(_getPlayCountKey(id), current + 1);
  }

  // Favorites
  bool isFavorite(String id) {
    return _prefs.getBool(_getFavKey(id)) ?? false;
  }

  Future<void> toggleFavorite(String id) async {
    final current = isFavorite(id);
    await _prefs.setBool(_getFavKey(id), !current);
  }
}
