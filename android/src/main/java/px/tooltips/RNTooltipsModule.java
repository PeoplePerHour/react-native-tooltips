
package px.tooltips;

import android.app.Activity;
import android.graphics.Color;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.Callback;
import com.facebook.react.bridge.ReadableMap;
import com.facebook.react.uimanager.UIManagerModule;
import com.facebook.react.uimanager.UIBlock;
import com.facebook.react.uimanager.NativeViewHierarchyManager;
import com.github.florent37.viewtooltip.ViewTooltip;

public class RNTooltipsModule extends ReactContextBaseJavaModule {

  private final ReactApplicationContext reactContext;
  private ViewTooltip tooltip;

  public RNTooltipsModule(ReactApplicationContext reactContext) {
    super(reactContext);
    this.reactContext = reactContext;
  }

  @Override
  public String getName() {
    return "RNTooltips";
  }

  @ReactMethod
  public void Show(final int targetId, final int parentId, final ReadableMap props, final Callback onHide) {
      this.reactContext.getNativeModule(UIManagerModule.class).prependUIBlock(new UIBlock() {

        @Override
        public void execute(NativeViewHierarchyManager nativeViewHierarchyManager) {

          final ViewGroup target = (ViewGroup) nativeViewHierarchyManager.resolveView(targetId);

          reactContext.runOnUiQueueThread(new Runnable() {
            @Override
            public void run() {
              if (target == null) {
                // it is possible that target end up being NULL
                // when findNodeHandle returns the wrong tag, findViewById won't be able to retrieve the view
                // there was an issue opened related to this problem 2 years ago, but it has never been fixed
                // https://github.com/facebook/react-native/issues/10385
                return;
              }

              String text = props.getString("text");
              String textColor = props.getString("textColor");
              String tintColor = props.getString("tintColor");
              boolean arrow = props.getBoolean("arrow");
              boolean autoHide = props.getBoolean("autoHide");
              boolean clickToHide = props.getBoolean("clickToHide");
              int align = props.getInt("align");
              int corner = props.getInt("corner");
              int duration = props.getInt("duration");
              int gravity = props.getInt("gravity");
              int paddingH = props.getInt("paddingHorizontal");
              int paddingV = props.getInt("paddingVertical");
              int position = props.getInt("position");
              int textSize = props.getInt("textSize");

              // parent reference is not required
              // ViewTooltip.on can retrieve the parent Context by itself
              tooltip = ViewTooltip.on(target);

              tooltip = tooltip.text(text)
                      .autoHide(autoHide, duration)
                      .clickToHide(clickToHide)
                      .color(Color.parseColor(tintColor))
                      .corner(corner)
                      .distanceWithView(0)
                      .padding(paddingH, paddingV, paddingH, paddingV)
                      .setTextGravity(gravity)
                      .textColor(Color.parseColor(textColor))
                      .textSize(TypedValue.COMPLEX_UNIT_SP, textSize);

              if (!arrow) {
                tooltip.arrowHeight(0);
                tooltip.arrowWidth(0);
              }

              switch (position) {
                case 1:
                  tooltip = tooltip.position(ViewTooltip.Position.LEFT);
                  break;
                case 2:
                  tooltip = tooltip.position(ViewTooltip.Position.RIGHT);
                  break;
                case 3:
                  tooltip = tooltip.position(ViewTooltip.Position.TOP);
                  break;
                case 4:
                default:
                  tooltip = tooltip.position(ViewTooltip.Position.BOTTOM);
              }

              switch (align) {
                case 2:
                  tooltip = tooltip.align(ViewTooltip.ALIGN.CENTER);
                  break;
                case 3:
                  tooltip = tooltip.align(ViewTooltip.ALIGN.END);
                  break;
                case 1:
                default:
                  tooltip = tooltip.align(ViewTooltip.ALIGN.START);
              }

              tooltip.onHide(new ViewTooltip.ListenerHide() {
                @Override
                public void onHide(View view) {
                  onHide.invoke();
                }
              });

              tooltip.show();
            }

          });
        }
      });


  
  }

  @ReactMethod
  public void Dismiss(final int view) {

    if (tooltip == null) {
      return;
  }

    tooltip.close();
    tooltip = null;
  }
}
